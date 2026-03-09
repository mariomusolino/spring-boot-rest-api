package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class TourGeneratorRequest {

    @NotBlank(message = "La destinazione è obbligatoria")
    private String destinazione; // nome del luogo da visitare

    @Positive(message = "Il tour deve durare almeno un giorno")
    @Max(value = 30, message = " La durata del tour non può superare 30 giorni")
    private int durata; // giorni di durata

    @NotBlank(message = "Specificare un livello di spesa")
    private String budget; // economico, lussuoso, etc...

    @NotBlank(message = "Specificare la tipologia del tour")
    private String type; // tipo di viaggio: culturale, gastronomico, relax

}
