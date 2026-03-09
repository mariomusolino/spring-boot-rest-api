package com.odissey.tour.model.dto.response;

import com.odissey.tour.model.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class CustomerResponse extends UserResponse{

    private String address;
    private String city;
    private String countryName;

    public CustomerResponse(int id, String email, String firstname, String lastname, String address, String city, String countryName) {
        super(id, email, firstname, lastname);
        this.address = address;
        this.city = city;
        this.countryName = countryName;
    }

    public static CustomerResponse fromEntityToDto(User user){
        return new CustomerResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getCustomer().getAddress(),
                user.getCustomer().getCity(),
                user.getCustomer().getCountry().getName()
        );
    }
}
