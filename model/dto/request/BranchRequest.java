package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class BranchRequest {

    @NotBlank(message = "Il nome dell'agenzia è obbligatorio e non può contenere solo spazi")
    private String name;

    @NotBlank(message = "La città è obbligatoria e non può contenere solo spazi")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Il nome della città può contenere solo caratteri")
    private String city;

    @NotBlank(message = "L'indirizzo' è obbligatoria e non può contenere solo spazi")
    private String address;

    @NotBlank(message = "Il VAT (Value Added Tax) è obbligatorio e non può contenere solo spazi")
    private String vat;

    @NotNull
    @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero")
    private int agencyId;
}
