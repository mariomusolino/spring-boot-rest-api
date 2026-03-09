package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odissey.tour.model.entity.Agency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // nel json di response
public class AgencyResponse {

    private int id;
    private String name;
    private String vat;
    private String fullAddress; // address + city
    private String countryName;

    public static AgencyResponse fromEntityToDto(Agency agency){
        return new AgencyResponse(
                agency.getId(),
                agency.getName(),
                agency.getVat(),
                agency.getCity().concat(" - ").concat(agency.getAddress()),
                agency.getCountry().getName()
        );
    }
}

