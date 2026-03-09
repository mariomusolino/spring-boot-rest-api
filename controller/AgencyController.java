package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.AgencyRequest;
import com.odissey.tour.model.dto.response.AgencyResponse;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.service.AgencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agencies")
@Validated
public class AgencyController {

    private final AgencyService agencyService;

    @Operation(
            summary = "CREATE AGENCY",
            description = "questo metodo serve ad inserire su database una nuova agenzia",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Agenzia inserita con successo sul database", content = @Content(schema = @Schema(implementation = AgencyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Uno dei seguenti valori di name, city, address o vat non è quello atteso", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Nazione con id specificato non trovata", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Vat dell'agenzia già presente su db",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<AgencyResponse> create(@RequestBody @Valid AgencyRequest req){
        return new ResponseEntity<>(agencyService.save(req), HttpStatus.CREATED);
    }

    @Operation(
            summary = "GET AGENCIES",
            description = "questo metodo serve a prendere tutte le agenzie sul database",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elenco delle agenzie sul database",content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgencyResponse.class)))),
                    @ApiResponse(responseCode = "404", description = "Nessuna agenzia al momento", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AgencyResponse>> getAgencies(){
        return new ResponseEntity<>(agencyService.findAllAgencies(), HttpStatus.OK);
    }

    @Operation(
            summary = "GET AGENCIES BY ID",
            description = "questo metodo serve a prendere un'agenzia attraverso un determinato id",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Agenzia trovata con id corrispondente", content = @Content(schema = @Schema(implementation = AgencyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Parametro non valido(id minore o uguale a 0)", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Nessuna agenzia trovata per l'id specificato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AgencyResponse> getAgency(
            @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int id
    ){
        return new ResponseEntity<>(agencyService.getAgency(id), HttpStatus.OK);
    }

    @Operation(
            summary = "GET AGENCIES BY COUNTRY ID",
            description = "questo metodo serve a prendere una lista di agenzie attraverso l'id di un country",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elenco delle agenzie con un id della country corrispondente",content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgencyResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Parametro non valido(id minore o uguale a 0)", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode ="404", description = "Nessuna agenzia trovata per l'id country specificato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/countries/{countryId}") // localhost:8081/agencies/countries/{countryId}
    public ResponseEntity<List<AgencyResponse>> getAgenciesByCountry(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero") short countryId
    ){
        return new ResponseEntity<>(agencyService.getAgenciesByCountry(countryId), HttpStatus.OK);
    }

    @Operation(
            summary = "UPDATE AGENCIES",
            description = "questo metodo serve ad aggiornare un'agenzia",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Agenzia aggiornata con successo",content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgencyResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Parametro non valido(id minore o uguale a 0)", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Nessuna agenzia o country trovati per un id specifico", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "VAT già presente a db", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),

            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AgencyResponse> update(
            @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int id,
            @RequestBody @Valid AgencyRequest req){
        return new ResponseEntity<>(agencyService.update(id, req), HttpStatus.OK);
    }

    @Operation(
            summary = "SWITCH ACTIVE STATUS",
            description = "questo metodo serve a cambiare status di un'agenzia da attivo a non attivo",
            tags = {"Agency"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Stato dell'agenzia aggiornato con successo"),
                    @ApiResponse(responseCode = "400", description = "Parametro non valido(id minore o uguale a 0)",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Nessuna agenzia trovata per l'id specificato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> switchAgencyStatus(
            @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int id
    ){
        agencyService.switchAgencyStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}