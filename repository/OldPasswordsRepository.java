package com.odissey.tour.repository;

import com.odissey.tour.model.entity.OldPasswords;
import com.odissey.tour.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OldPasswordsRepository extends JpaRepository<OldPasswords, Integer> {

    List<OldPasswords> findTop3ByUserOrderByLastChangePasswordDesc(User user);
    List<OldPasswords> findByUserOrderByLastChangePasswordDesc(User user);
}
