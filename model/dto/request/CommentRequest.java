package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentRequest {

    @NotNull
    @Min(value = 1, message = "L'id del tour deve essere un numero intero positivo")
    private int tourId;

    @NotBlank(message = "Il testo del commento è obbligatorio")
    @Size(min = 2, max = 255, message = "La lunghezza del commento è compresa tra 2 e 255 caratteri")
    private String content;

    @Min(value = 1, message = "L'id del commento di riferimento deve essere un numero intero positivo")
    private Integer refererTo;

}
