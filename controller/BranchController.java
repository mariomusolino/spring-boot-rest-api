package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.BranchRequest;
import com.odissey.tour.model.dto.response.BranchResponse;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/branches")
@Validated
public class BranchController {

    private final BranchService branchService;

    @Operation(
            summary = "CREATE BRANCH",
            description = "questo metodo serve ad inserire su database una nuova agenzia",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Agenzia inserita con successo sul database", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Uno dei seguenti valori di name, city, address o vat non è quello atteso", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Agenzia con id specificato non trovata", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Uno dei seguenti valori tra vat,agency e name  è già presente a database",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Agenzia di riferimento disattivata",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<BranchResponse> save(@RequestBody @Valid BranchRequest req){
        return new ResponseEntity<>(branchService.save(req), HttpStatus.CREATED);
    }

    @Operation(
            summary = "GET BRANCHES BY AGENCY ID",
            description = "questo metodo serve a prendere tutte le filiali sul database dato un id dell'agenzia specificato",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elenco delle filiali sul database per una determinata agenzia",content = @Content(array = @ArraySchema(schema = @Schema(implementation = BranchResponse.class)))),
                    @ApiResponse(responseCode = "404", description = "Filiali non trovate al momento per una determinata agenzia", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/agencies/{agencyId}")
    public ResponseEntity<List<BranchResponse>> getBranchesByAgency(
            @PathVariable @Min(value = 1, message = "L'id dell'agenzia deve essere un numero intero maggiore di zero") int agencyId
    ){
        List<BranchResponse> list = branchService.getBranchesByAgency(agencyId);
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Operation(
            summary = "GET BRANCHES BY COUNTRY ID",
            description = "questo metodo serve a prendere tutte le filiali sul database dato un id di una country",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elenco delle filiali sul database per un determinato country",content = @Content(array = @ArraySchema(schema = @Schema(implementation = BranchResponse.class)))),
                    @ApiResponse(responseCode = "404", description = "Filiali non trovate al momento per un determinato country", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("countries/{countryId}")
    public ResponseEntity<List<BranchResponse>> getBranchesByCountry(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di zero") short countryId
    ){
        List<BranchResponse> list = branchService.getBranchesByCountry(countryId);
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Operation(
            summary = " UPDATE BRANCH",
            description = "questo metodo serve ad aggiornare una filiale sul database dato un id specifico",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elenco delle filiali sul database per una determinata agenzia",content = @Content(array = @ArraySchema(schema = @Schema(implementation = BranchResponse.class)))),
                    @ApiResponse(responseCode = "404", description = "Filiali non trovate al momento per una determinata agenzia", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Uno dei seguenti valori tra vat,agency e name è già presente a database",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> update(
            @RequestBody @Valid BranchRequest req,
            @PathVariable @Min(value = 1, message = "L'id della filiale deve essere un numero intero maggiore di zero") int id
    ){
        return new ResponseEntity<>(branchService.update(req, id), HttpStatus.OK);
    }

    @Operation(
            summary = "SWITCH ACTIVE STATUS",
            description = "questo metodo serve a cambiare status di una filiale da attivo a non attivo",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Stato della filiale aggiornato con successo"),
                    @ApiResponse(responseCode = "400", description = "Parametro non valido(id minore o uguale a 0)",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Nessuna filiale trovata per l'id specificato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> switchBranchStatus(
            @PathVariable @Min(value = 1, message = "L'id della filiale deve essere un numero intero maggiore di zero") int id
    ){
        branchService.switchBranchStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "GET BRANCH",
            description = "questo metodo restituisce una filiale in base all'id",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filiale trovata con successo",content = @Content(array = @ArraySchema(schema = @Schema(implementation = BranchResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Parametro non valido(id minore o uguale a 0)",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Nessuna filiale trovata per l'id specificato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getBranch(
            @PathVariable @Min(value = 1, message = "L'id della filiale deve essere un numero intero maggiore di zero") int id
    ){
        return new ResponseEntity<>(branchService.getBranch(id), HttpStatus.OK);
    }

    @Operation(
            summary = "SET API KEY",
            description = "questo metodo serve a salvare e collegare una filiale con un api key per usufruire del servizio di AI tour generator",
            tags = {"Branch"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Api key salvata correttamente",content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
                    @ApiResponse(responseCode = "400", description = "L'id della filiale o l'api key non rappresentano un valore valido",content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Nessuna filiale trovata per l'id specificato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{branchId}/api-key")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    public ResponseEntity<String> setApiKey(
            @PathVariable @Min(value = 1, message = "L'id della filiale deve essere un numero intero maggiore di zero") int branchId,
            @RequestBody @NotBlank String apiKey
    ){
        return new ResponseEntity<>(branchService.setApiKey(branchId, apiKey), HttpStatus.OK);
    }
}