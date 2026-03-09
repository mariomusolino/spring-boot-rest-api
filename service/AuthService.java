package com.odissey.tour.service;

import com.odissey.tour.exception.*;
import com.odissey.tour.model.GenericMail;
import com.odissey.tour.model.dto.request.CustomerRequest;
import com.odissey.tour.model.dto.request.LoginRequest;
import com.odissey.tour.model.dto.request.ResetPasswordRequest;
import com.odissey.tour.model.dto.response.LoginResponse;
import com.odissey.tour.model.entity.*;
import com.odissey.tour.model.entity.enumerator.Role;
import com.odissey.tour.repository.CountryRepository;
import com.odissey.tour.repository.LoginTracesRepository;
import com.odissey.tour.repository.OldPasswordsRepository;
import com.odissey.tour.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginTracesRepository loginTracesRepository;
    private final OldPasswordsRepository oldPasswordsRepository;
    private final EmailService emailService;
    private final CountryRepository countryRepository;

    @Value(("${passwordExpireInDays}"))
    private long passwordExpireInDays;

    @Transactional
    public String otpVerification(String otp, String email){
        // Verificare che su db esiste una coppia otp - email
        User user = userRepository.otpVerification(otp, email)
                .orElseThrow(()-> new Exception404("Email o codice otp non corretti"));
        user.setOtpCode(null);
        user.setVerified(true);
        user.setEnabled(true);
        LocalDateTime now = LocalDateTime.now();
        user.setLastChangePassword(now);
        userRepository.save(user);
        oldPasswordsRepository.save(new OldPasswords(user, user.getPassword(), now));
        return ("Complimenti " + user.getFirstname() + " " + user.getLastname() + ", verifica avvenuta con successo. Ora puoi loggarti!");
    }

    public LoginResponse login(LoginRequest req){
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(()-> new Exception403("Credenziali errate"));
        if(!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new Exception403("Credenziali errate.");
        if(!user.isEnabled() && user.isVerified())
            throw new Exception401("Utenza disabilitata. Contattare l'amministratore.");
        if(!user.isEnabled() && !user.isVerified())
            throw new Exception401("Ti è stata inviata un'email il giorno " + user.getCreatedAt().toLocalDate() +  " per verificare la tua registrazione. Effettua la verifica per poter loggarti.");
        if(user.getLastChangePassword().plusDays(passwordExpireInDays).isBefore(LocalDateTime.now()))
            throw new Exception401("La tua password è scaduta. Procedi al reset.");

        loginTracesRepository.save(new LoginTraces(user));

        LoginResponse res = new LoginResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setRole(user.getRole().name());
        res.setJwt(jwtService.generateAccessToken(user));

        return res;
    }

    public String requestResetPassword(String email) {
        // verifico l'esistenza dell'utente associato all'email
        User user = userRepository.findByEmailAndEnabledTrueAndVerifiedTrue(email)
                .orElseThrow(()-> new Exception404("Utente inesistente o non verificato o disabilitato"));
        String otpCode = UserService.generateOtpCode(6);
        GenericMail mail = new GenericMail();
        mail.setTo(user.getEmail());
        mail.setSubject("Reset password");
        mail.setBody("Gentile " + user.getFirstname() + " " + user.getLastname() + ",\nclicca sul seguente link per resettare la password.\nIl tuo codice otp è: " + otpCode);
        try {
            emailService.sendMail(mail);
            user.setVerified(false);
            user.setEnabled(false);
            user.setOtpCode(otpCode);
            userRepository.save(user);
        }catch(MessagingException e){
            throw new Exception500("Errore nell'invio dell'email");
        }
        return "Richiesta di reset password inviata correttamente. Controlla il tuo account di posta elettronica per procedere.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest req) {
        User user = userRepository.otpVerification(req.getOtpCode(), req.getEmail())
                .orElseThrow(()-> new Exception404("Utente non trovato"));
        if(!req.getPassword1().equals(req.getPassword2()))
            throw new Exception400("Le password inserite non coincidono");

        List<OldPasswords> oldPasswordsList = oldPasswordsRepository.findTop3ByUserOrderByLastChangePasswordDesc(user);
        for(OldPasswords o : oldPasswordsList){
            if(passwordEncoder.matches(req.getPassword1(), o.getOldPassword()))
                throw new Exception400("La password scelta è già stata utilizzata in uno dei tre precedenti cambi");
        }

        LocalDateTime now = LocalDateTime.now();

        user.setVerified(true);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(req.getPassword1()));
        user.setOtpCode(null);
        user.setLastChangePassword(now);

        oldPasswordsRepository.save(new OldPasswords(user, user.getPassword(), now));

        List<OldPasswords> all = oldPasswordsRepository.findByUserOrderByLastChangePasswordDesc(user);
        if (all.size() > 3) {
            oldPasswordsRepository.deleteAll(all.subList(3, all.size()));
        }

        return "Password resettata con successo!";
    }

    @Transactional
    public String customerSignup(CustomerRequest req) {
        // Verifica che non esistano già utenti con quella username o email
        String email = req.getEmail().trim();
        String username = req.getUsername().trim();
        LocalDateTime now = LocalDateTime.now();

        if(userRepository.existsByUsernameOrEmail(username, email))
            throw new Exception409("Un utente con email "+email+" o username "+username+" esiste già a sistema.");

        // Istanzio oggetto user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstname(req.getFirstname().trim());
        user.setLastname((req.getLastname().trim()));
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setOtpCode(UserService.generateOtpCode(6));
        user.setRole(Role.CUSTOMER);
        user.setLastChangePassword(now);

        // Verficare esistenza country
        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id: " + req.getCountryId()));

        // Istanzio oggetto Customer
        Customer customer = new Customer();
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());
        customer.setCountry(country);

        // Sincronizzazione
        user.setCustomer(customer);

        try {
            // Salvataggio
            userRepository.save(user);

            // old passwords (lo facciamo poi in otpVerification)
           // oldPasswordsRepository.save(new OldPasswords(user, user.getPassword(), now));

            // invio otp code via email
            emailService.sendMail(UserService.sendOtp(user));
        } catch (MessagingException e){
            log.error(">>> "+e.getMessage());
            throw new Exception500("Si è verificato un errore nell'invio dell'email");
        } catch (Exception e){
            log.error(">>> "+e.getMessage());
            throw new Exception500("Si è verificato un errore durante la fase di registrazione");
        }
        return "Registrazione avvenuta con successo. Controllare account di posta elettronica per la verifica dell'email";
    }
}
