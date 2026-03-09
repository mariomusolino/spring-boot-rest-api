package com.odissey.tour.configuration;

import com.odissey.tour.exception.Exception404;
import com.odissey.tour.model.entity.enumerator.Role;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitApp {

    private final UserRepository userRepository;

    @Bean
    public boolean insertFirstAdmin(){
        log.info(">>> Verifico le condizioni per inserire il primo admin a sistema.");
        if(!userRepository.existsById(1)) {
            User user = new User();
            user.setEmail("admin@tour-odissey.abc");
            user.setEnabled(true);
            user.setFirstname("Deus");
            user.setLastname("Ex machina");
            user.setPassword("123456");
            user.setRole(Role.ADMIN);
            user.setUsername("admin");
            user.setVerified(true);
            userRepository.save(user);
            log.info(">>> L'utente "+user.getFirstname()+ " "+user.getLastname()+" è stato inserito con successo");
            return true;
        }
        log.info(">>> Nessun utente di tipo ADMIN aggiunto in quanto già presente");
        return false;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception404("Utente non trovato"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

}
