package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class VoucherRequest {

    @NotBlank(message = "Nome e cognome di chi regala il voucher è obbligatorio.")
    private String emittedBy;

    @NotNull(message = "L'id del customer è obbligatorio.")
    @Min(value = 1, message = "L'id del customer deve essere un numero positivo maggiore di zero.")
    private int customerId;

    @NotNull(message = "Il prezzo è obbligatorio.")
    @Digits(integer = 4, fraction = 2, message = "Il prezzo non può essere maggiore di 9999,99")
    @Positive(message = "Il prezzo deve essere positivo e maggiore di zero")
    private float price;

    @NotBlank(message = "La causale del voucher è obbligatoria")
    private String type;
}
