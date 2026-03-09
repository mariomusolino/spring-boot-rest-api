package com.odissey.tour.service;

import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.model.dto.request.CustomerUpdateRequest;
import com.odissey.tour.model.dto.response.CustomerDetailResponse;
import com.odissey.tour.model.dto.response.CustomerListResponse;
import com.odissey.tour.model.dto.response.CustomerResponse;
import com.odissey.tour.model.entity.Country;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.model.entity.enumerator.Role;
import com.odissey.tour.model.entity.User;
import com.odissey.tour.repository.CountryRepository;
import com.odissey.tour.repository.CustomerRepository;
import com.odissey.tour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CustomerRepository customerRepository;

    public CustomerResponse updateCustomer(UserDetails userDetails, CustomerUpdateRequest req){
        // Verificare che username e email non siano già in uso
        User user = (User) userDetails;

        String username = req.getUsername().trim();
        String email = req.getEmail().trim();

        if(userRepository.existsByIdNotAndUsernameOrEmail(user.getId(), username, email) > 0)
            throw new Exception409("Un utente con email " + email + " o username " + username + " esiste già a sistema.");

        user.setEmail(email);
        user.setUsername(username);
        user.setFirstname(req.getFirstname().trim());
        user.setLastname(req.getLastname().trim());

        // Verificare esistenza country
        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id: " + req.getCountryId()));

        // Istanziare oggetto Customer..
        Customer customer = new Customer();
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());
        customer.setCountry(country);

        // ..e settarlo su user
        user.setCustomer(customer);

        // Aggiornare l'utente customer
        userRepository.save(user);

        return CustomerResponse.fromEntityToDto(user);
    }

    public List<CustomerListResponse> getActiveCustomersByLastLoginAndCountry() {
        return customerRepository.getActiveCustomersByLastLoginAndCountry(Role.CUSTOMER);
    }

    public CustomerDetailResponse getActiveCustomerByLastLoginAndCountryAndId(int id) {
        return customerRepository.getActiveCustomerByLastLoginAndCountryAndId(id, Role.CUSTOMER)
                .orElseThrow(()-> new Exception404("Customer non trovato con id: " + id));
    }
}
