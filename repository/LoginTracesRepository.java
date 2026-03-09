package com.odissey.tour.repository;

import com.odissey.tour.model.entity.LoginTraces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoginTracesRepository extends JpaRepository<LoginTraces, Integer> {

    @Query("SELECT " +
            "CAST (MAX(lt.loginDate) AS string) FROM LoginTraces lt " +
            "WHERE lt.user.id = :userId")
    Optional<String> getLastLoginByUser(int userId);
}
