package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    @NotBlank(message = "Username obbligatorio")
    @Size(max = 30, min = 3, message = "La lunghezza deve essere compresa tra 3 e 30 caratteri")
    private String username;

    @NotBlank(message = "Email obbligatoria")
    private String email;

    @NotBlank(message = "Nome obbligatorio")
    @Size(max = 255, min = 2, message = "La lunghezza deve essere compresa tra 2 e 255 caratteri")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$", message = "Il nome deve contenere solo caratteri alfabetici")
    private String firstname;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(max = 255, min = 2, message = "La lunghezza deve essere compresa tra 2 e 255 caratteri")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$", message = "Il cognome deve contenere solo caratteri alfabetici")
    private String lastname;

}
