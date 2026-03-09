package com.odissey.tour.model.entity.listeners;

import com.odissey.tour.model.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {

        // Ottiene l'oggetto Authentication (l'utente loggato) dal contesto di Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Controlla se l'utente è autenticato e non è un anonimo
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            // Se non autenticato, restituisce Optional.empty()
            return Optional.empty();
        }

        // Esempio: Supponiamo che la tua classe User personalizzata implementi UserDetails e abbia un metodo getId()
        if (authentication.getPrincipal() instanceof Integer id) { // Se l'oggetto principal è già l'ID (caso raro)
            return Optional.of(id);
        } else if (authentication.getPrincipal() instanceof User userPrincipal) {
            return Optional.of(userPrincipal.getId()); // Sostituisci con il tuo metodo per l'ID
        }

        // Fallback: qui dovresti gestire l'estrazione dell'ID dall'oggetto principal
        // Per questo esempio, restituiamo un Optional vuoto
        return Optional.empty();
    }
}
