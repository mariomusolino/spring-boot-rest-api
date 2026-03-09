package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odissey.tour.model.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BranchResponse {

    // membri della classe Branch
    private Integer id;
    private String name;
    private String city;
    private String address;
    private String vat;

    // membri della classe Agency
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer agencyId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String agencyName;

    // membri della classe Country
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countryName;

    public BranchResponse(Integer id, String name, String city, String address, String vat, String countryName) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.vat = vat;
        this.countryName = countryName;
    }

    public BranchResponse(Integer id, String name, String city, String address, String vat, Integer agencyId, String agencyName) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.vat = vat;
        this.agencyId = agencyId;
        this.agencyName = agencyName;
    }

    public static BranchResponse fromEntityToDto(Branch branch){
        return new BranchResponse(
                branch.getId(),
                branch.getName(),
                branch.getCity(),
                branch.getAddress(),
                branch.getVat(),
                branch.getAgency().getId(),
                branch.getAgency().getName(),
                branch.getAgency().getCountry().getName()
        );
    }
}
