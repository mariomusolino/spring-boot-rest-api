package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.exception.Exception500;
import com.odissey.tour.model.GenericMail;
import com.odissey.tour.model.dto.request.ChangePasswordRequest;
import com.odissey.tour.model.dto.request.UserRoleRequest;
import com.odissey.tour.model.dto.request.UserUpdateRequest;
import com.odissey.tour.model.dto.response.UserDetailResponse;
import com.odissey.tour.model.entity.OldPasswords;
import com.odissey.tour.model.entity.enumerator.Role;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.repository.LoginTracesRepository;
import com.odissey.tour.repository.OldPasswordsRepository;
import com.odissey.tour.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LoginTracesRepository loginTracesRepository;
    private final OldPasswordsRepository oldPasswordsRepository;

    public UserDetailResponse register(UserRoleRequest req){
        String username = req.getUsername().trim();
        String email = req.getEmail().trim();
        String role = req.getRole().trim().toUpperCase();

        // verificare che non esista altro utente con stessa username o email
        if(userRepository.existsByUsernameOrEmail(username, email))
            throw new Exception409("Un utente con email "+email+" o username "+username+" esiste già a sistema.");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstname(req.getFirstname().trim());
        user.setLastname((req.getLastname().trim()));
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setOtpCode(generateOtpCode(6));
        try {
            user.setRole(Role.valueOf(role));
        } catch (Exception ex){
            log.error(">>> {}", ex.getMessage());
            throw new Exception400("Ruolo non valido");
        }
        try {
            userRepository.save(user);
            emailService.sendMail(sendOtp(user));
        } catch(MessagingException e){
            log.error(">>> "+e.getMessage());
            throw new Exception500("Si è verificato un errore nell'invio dell'email");
        }
        return UserDetailResponse.fromEntityToDto(user);
    }


    public String resendOtpCode(int id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new Exception404("Utente non trovato con id "+id));
        user.setOtpCode(generateOtpCode(6));
        user.setEnabled(false);
        user.setVerified(false);
        try {
            userRepository.save(user);
            emailService.sendMail(sendOtp(user));
        } catch(MessagingException e){
            log.error(">>> "+e.getMessage());
            throw new Exception500("Si è verificato un errore nell'invio dell'email");
        }
        return "Codice OTP inviato correttamnte a "+user.getEmail();
    }

    public String enableDisableUser(int id, UserDetails userDetails){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new Exception404("Utente non trovato con id " + id));
        if(userDetails.getUsername().equals(user.getUsername()))
            throw new Exception409("Non puoi disabilitare te stesso");
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        String status = user.isEnabled() ? "abilitato" : "disabilitato";
        return "L'utente è stato "+status+" con successo";
    }

    public UserDetailResponse update(UserDetails userDetails, UserUpdateRequest req){
        User user = (User) userDetails;

        String username = req.getUsername().trim();
        String email = req.getEmail().trim();

        if(userRepository.existsByIdNotAndUsernameOrEmail(user.getId(), username, email) > 0)
            throw new Exception409("Un utente con email " + email + " o username " + username + " esiste già a sistema.");

        user.setEmail(email);
        user.setUsername(username);
        user.setFirstname(req.getFirstname().trim());
        user.setLastname(req.getLastname().trim());

        userRepository.save(user);
        return UserDetailResponse.fromEntityToDto(user);
    }

    public String lastLoginByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new Exception404("Utente non trovato con id " + userId));
        return loginTracesRepository.getLastLoginByUser(userId)
                .orElseThrow(()-> new Exception404("L'utente con id " + userId + " non si è mai loggato"));
    }

    @Transactional
    public String changePassword(UserDetails userDetails, ChangePasswordRequest req) {
        User user = (User) userDetails;
        // verifico che la vecchia password coincida con quella del db
        if(!passwordEncoder.matches(req.getOldPassword(), user.getPassword()))
            throw new Exception400("La vecchia password è errata");
        // verifico che la nuova password e la sua ripetizione coincidano
        if(!req.getPassword1().trim().equals(req.getPassword2().trim()))
            throw new Exception400("La nuova password e la sua ripetizione non coincidono");
        // verifico che la password scelta sia diversa dalle 3 precedenti
        List<OldPasswords> oldPasswords = oldPasswordsRepository.findTop3ByUserOrderByLastChangePasswordDesc(user);
        for(OldPasswords o : oldPasswords){
            if(passwordEncoder.matches(req.getPassword1(), o.getOldPassword()))
                throw new Exception409("La password scelta è già stata utilizzata in uno dei tre precedenti cambi password");
        }

        String newPassword = passwordEncoder.encode(req.getPassword1().trim());
        LocalDateTime now = LocalDateTime.now();
        user.setPassword(newPassword);
        user.setLastChangePassword(now);
        userRepository.save(user);

        oldPasswordsRepository.save(new OldPasswords(user, newPassword, now));

        List<OldPasswords> all = oldPasswordsRepository.findByUserOrderByLastChangePasswordDesc(user);
        if (all.size() > 3) {
            oldPasswordsRepository.deleteAll(all.subList(3, all.size()));
        }

        return "La password è stata aggiornata con successo!";
    }

    // -----------------------------------------------------------


    public static String generateOtpCode(int lunghezza) {
        String numeri = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(lunghezza);

        for (int i = 0; i < lunghezza; i++) {
            sb.append(numeri.charAt(random.nextInt(numeri.length())));
        }
        return sb.toString();
    }

    public static GenericMail sendOtp(User user){
        GenericMail mail = new GenericMail();
        mail.setTo(user.getEmail());
        mail.setSubject("Tour Odissey: conferma di registrazione");
        mail.setBody("Gentile "+user.getFirstname()+" "+user.getLastname()+",\nal fine di confermare la registrazione, clicca sul seguente <a href=''>link</a> ed inserisci il codice otp "+user.getOtpCode());
        return mail;
    }

}
