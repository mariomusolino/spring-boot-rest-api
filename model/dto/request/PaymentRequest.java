package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentRequest {

    @NotNull
    @Min(value = 1, message = "L'id del tour deve essere un numero positivo maggiore di zero")
    private int tourId;

    @NotNull
    @Min(value = 1, message = "L'id del customer deve essere un numero positivo maggiore di zero")
    private int customerId;

    @Min(value = 1, message = "L'id del voucher deve essere un numero positivo maggiore di zero")
    private Integer voucherId;

    @NotBlank(message = "Il tipo di pagamento è obbligatorio")
    private String paymentType;

    private Float amount;
}
