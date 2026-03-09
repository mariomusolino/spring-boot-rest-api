package com.odissey.tour.repository;

import com.odissey.tour.model.dto.response.CustomerDetailResponse;
import com.odissey.tour.model.dto.response.CustomerListResponse;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.enumerator.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT new com.odissey.tour.model.dto.response.CustomerListResponse(" +
            "u.id, " +
            "u.firstname, " +
            "u.lastname, " +
            "u.customer.country.name, " +
            "MAX(lt.loginDate)) " +
            "FROM User u " +
            "LEFT JOIN LoginTraces lt ON lt.user.id = u.id " +
            "WHERE u.verified = true AND u.enabled = true AND u.role = :role " +
            "GROUP BY u.id, u.firstname, u.lastname, u.customer.country.name"
    )
    List<CustomerListResponse> getActiveCustomersByLastLoginAndCountry(Role role);

    @Query("SELECT new com.odissey.tour.model.dto.response.CustomerDetailResponse(" +
            "u.id, " +
            "u.email, " +
            "u.firstname, " +
            "u.lastname, " +
            "u.customer.address, " +
            "u.customer.city, " +
            "u.customer.country.name, " +
            "MAX(lt.loginDate)) " +
            "FROM User u " +
            "LEFT JOIN LoginTraces lt ON u.id = lt.user.id " +
            "WHERE u.verified = true AND u.enabled = true AND u.id = :id AND u.role = :role " +
            "GROUP BY u.id, u.email, u.firstname, u.lastname, u.customer.address, u.customer.city, u.customer.country.name"
    )
    Optional<CustomerDetailResponse> getActiveCustomerByLastLoginAndCountryAndId(int id, Role role);
}
