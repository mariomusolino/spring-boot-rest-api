package com.odissey.tour.repository;

import com.odissey.tour.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsernameOrEmail(String username, String email);

    Optional<User> findByEmailAndEnabledTrueAndVerifiedTrue(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.otpCode = :otp AND u.email = :email")
    Optional<User> otpVerification(String otp, String email);

    @Query("SELECT COUNT(u.id) FROM User u WHERE NOT u.id = :id AND (u.username = :username OR u.email = :email)")
    long existsByIdNotAndUsernameOrEmail(int id, String username, String email);

 /*   @Query("SELECT u from User u " +
            "INNER JOIN Customer c ON c.id = u.id " +
            "JOIN FETCH c.country " +
            "WHERE u.id = :id")
    Optional<User> findCustomerByUserId(int id);

  */

}
