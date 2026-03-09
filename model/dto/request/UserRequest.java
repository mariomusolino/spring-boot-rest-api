package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRequest {

    @NotBlank(message = "Username obbligatorio")
    @Size(max = 30, min = 3, message = "La lunghezza deve essere compresa tra 3 e 30 caratteri")
    private String username;

    @NotBlank(message = "Email obbligatoria")
    private String email;

    @NotBlank(message = "Nome obbligatorio")
    @Size(max = 255, min = 2, message = "La lunghezza deve essere compresa tra 2 e 255 caratteri")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Il nome può contenere solo caratteri")
    private String firstname;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(max = 255, min = 2, message = "La lunghezza deve essere compresa tra 2 e 255 caratteri")
    @Pattern(regexp = "^[\\p{L}\\s']+$", message = "Il nome può contenere solo caratteri e apostrofi")
    private String lastname;

    @NotBlank(message = "Password obbligatoria")
    @Size(min = 8, max = 16, message = "La password deve essere lunga almeno 8 caratteri e non più di 16")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$",
            message = "La password deve contenere almeno un numero, un carattereminuscolo, un carattere maiuscolo e almeno un carattere speciale tra questi")
    private String password;

}
