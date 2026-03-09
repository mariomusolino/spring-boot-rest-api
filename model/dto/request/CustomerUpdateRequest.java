package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CustomerUpdateRequest extends UserUpdateRequest{

    @NotBlank(message = "L'indirizzo è obbligatorio")
    private String address;

    @NotBlank(message = "La città è obbligatoria")
    @Pattern(regexp = "^[\\p{L}\\s']+$", message = "Il nome può contenere solo caratteri e apostrofi")
    private String city;

    @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero")
    @NotNull(message = "L'identificativo della nazione è obbligatorio")
    private short countryId;
}