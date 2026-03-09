package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.VoucherRequest;
import com.odissey.tour.model.dto.response.BranchResponse;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.model.dto.response.VoucherReceiptResponse;
import com.odissey.tour.service.VoucherService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/vouchers")
public class VoucherController {

    private final VoucherService voucherService;


    @Operation(
            summary = "CREATE VOUCHER",
            description = "Questo metodo serve a creare un voucher del tipo 'gift'.",
            tags = {"Voucher"},
            responses = {
                    @ApiResponse(responseCode="201", description="Voucher creato correttamente.", content = @Content(schema = @Schema(implementation = VoucherReceiptResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di emittedBy, type, customerId, price non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Customer di riferimento non trovato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<VoucherReceiptResponse> create(@RequestBody @Valid VoucherRequest req){
        return new ResponseEntity<>(voucherService.create(req), HttpStatus.CREATED);
    }



}
