package com.odissey.tourai.repository;

import com.odissey.tourai.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    boolean existsByApiKeyAndExpirationDateAfter(String apiKey, LocalDate now);
}
