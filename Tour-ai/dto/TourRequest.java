package com.odissey.tourai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TourRequest {

    @NotBlank(message = "La destinazione è obbligatoria")
    private String destinazione;

    @Positive(message = "Il tour deve durare almeno un giorno")
    @Max(value = 30, message = "La durata del tour non può superare 30 giorni")
    private int durata;

    @NotBlank(message = "Specificare un livello di spesa")
    private String budget;

    @NotBlank(message = "Specificare la tipologia del tour")
    private String type;
}
