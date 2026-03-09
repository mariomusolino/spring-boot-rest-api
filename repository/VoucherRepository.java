package com.odissey.tour.repository;

import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    Optional<Voucher> findByIdAndCustomerIsAndUsedFalse(int id, Customer customer);
}
