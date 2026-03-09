package com.odissey.tour.repository;

import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.Payment;
import com.odissey.tour.model.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query(value = "SELECT SUM(p.amount) FROM payments p " +
            "WHERE p.tour_id = :tourId AND p.customer_id = :customerId", nativeQuery = true)
    Float sumPaymentByTourAndCustomer(int tourId, int customerId);
}
