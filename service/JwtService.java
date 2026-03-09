package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception401;
import com.odissey.tour.model.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    String secret;
    @Value("${jwt.expireInMinutes}")
    long expiration;
    @Value(("${spring.application.name}"))
    String issuer;


    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        String role = user.getAuthorities().stream().findFirst().map(r -> r.getAuthority())
                .orElseThrow(()-> new Exception401("Nessun ruolo associato all'utente"));
        return Jwts.builder()
                .issuer(issuer) // chi emette il token
                .subject(user.getUsername())
                .claims(Map.of("role", role))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(240L, ChronoUnit.MINUTES)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch(Exception e){
            throw new Exception401("Token non valido o scaduto. Rieseguire l'autenticazione.");
        }
    }

}
