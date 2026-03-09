package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odissey.tour.model.entity.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // nel json di response
public class CountryResponse {

    private short id;
    private String code;
    private String name;
    private String currency;

    public static CountryResponse fromEntityToDto(Country country){
        return new CountryResponse(
                country.getId(),
                country.getCode(),
                country.getName(),
                country.getCurrency()
        );
    }

    public CountryResponse(short id, String name){
        this.id = id;
        this.name = name;
    }

}
