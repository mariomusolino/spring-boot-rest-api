package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {

    @NotBlank(message = "La vecchia password è obbligatoria")
    private String oldPassword;

    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 8, max = 16, message = "La password deve essere lunga almeno 8 caratteri e non più di 16")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$",
            message = "La password deve contenere almeno un numero, un carattereminuscolo, un carattere maiuscolo e almeno un carattere speciale tra questi")
    private String password1;

    @NotBlank(message = "La ripetizione della nuova password è obbligatoria")
    @Size(min = 8, max = 16, message = "La password deve essere lunga almeno 8 caratteri e non più di 16")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+=!]).*$",
            message = "La password deve contenere almeno un numero, un carattereminuscolo, un carattere maiuscolo e almeno un carattere speciale tra questi")
    private String password2;
}
