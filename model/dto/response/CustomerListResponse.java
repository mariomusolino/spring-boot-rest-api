package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*Utenti attivi di tipo CUSTOMER*/

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CustomerListResponse {

    private int id;
    private String firstname;
    private String lastname;
    private String countryCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;
}
