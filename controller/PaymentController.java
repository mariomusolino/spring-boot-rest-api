package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.PaymentRequest;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.model.dto.response.CustomerResponse;
import com.odissey.tour.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "CREATE PAYMENT",
            description = "Questo metodo serve ad eseguire il pagamento parziale o totale di un tour da parte di un customer.",
            tags = {"Payment"},
            responses = {
                    @ApiResponse(responseCode="201", description="Pagamento avvenuto con successo.", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di customerId, tourId, VoucherType, voucherId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Tour o customer o voucher non trovati.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Il tour è già stato pagato per intero o il pagamento eccede il valore totale del tour.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<String> create(@RequestBody @Valid PaymentRequest req){
        return new ResponseEntity<>(paymentService.create(req), HttpStatus.CREATED);
    }
}
