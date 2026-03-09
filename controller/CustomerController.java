package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.CustomerUpdateRequest;
import com.odissey.tour.model.dto.response.*;
import com.odissey.tour.model.entity.Customer;
import com.odissey.tour.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "UPDATE CUSTOMER",
            description = "Questo metodo consete ad un utente (autenticato) di aggiornare le proprie informazioni realtive a username, email, firstname, lastname, address, city o country.",
            tags = {"Customer"},
            responses = {
                    @ApiResponse(responseCode="200", description="Aggiornamento avvenuto con successo", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di username, email, firstname, lastname, address, city o country non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Username o email già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PutMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CustomerUpdateRequest req
    ){
        return new ResponseEntity<>(customerService.updateCustomer(userDetails, req), HttpStatus.OK);
    }

    @Operation(
            summary = "GET ACTIVE CUSTOMER",
            description = "Questo metodo restituisce una lista di utenti di tipo customer",
            tags = {"Customer"},
            responses = {
                    @ApiResponse(responseCode="200", description="Lista dei customer attivi restituita con successo", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerListResponse.class)))),
                    @ApiResponse(responseCode="404", description="Nessun utente di tipo customer presente a db", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerListResponse.class))))
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<CustomerListResponse>> getActiveCustomersByLastLoginAndCountry(){
        List<CustomerListResponse> list = customerService.getActiveCustomersByLastLoginAndCountry();
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Operation(
            summary = "GET ACTIVE CUSTOMER DETAIL",
            description = "Questo metodo restituisce il dettaglio di un utente di tipo customer",
            tags = {"Customer"},
            responses = {
                    @ApiResponse(responseCode="200", description="Dettaglio del customer attivo restituita con successo", content = @Content(schema = @Schema(implementation = CustomerDetailResponse.class))),
                    @ApiResponse(responseCode="404", description="Customer non trovato o non attivo", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="400", description="Id customer non è quello atteso", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<CustomerDetailResponse> getActiveCustomersByLastLoginAndCountryAndId(
            @PathVariable @NotNull(message = "L'identificativo è obbligatorio") @Min(value = 1, message = "L'id dell'utente deve essere un numero intero maggiore di zero") int id
    ){
        return new ResponseEntity<>(customerService.getActiveCustomerByLastLoginAndCountryAndId(id), HttpStatus.OK);
    }

}
