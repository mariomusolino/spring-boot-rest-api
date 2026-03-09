package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class CustomerDetailResponse extends CustomerResponse{

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    public CustomerDetailResponse(int id, String email, String firstname, String lastname, String address, String city, String countryName, LocalDateTime lastLogin) {
        super(id, email, firstname, lastname, address, city, countryName);
        this.lastLogin = lastLogin;
    }
}
