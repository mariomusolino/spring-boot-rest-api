package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.CountryRequest;
import com.odissey.tour.model.dto.response.CountryResponse;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.service.CountryService;
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
@RequestMapping("/countries")
@Validated
public class CountryController {

    private final CountryService countryService;

    @Operation(
            summary = "CREATE COUNTRY",
            description = "Questo metodo serve a inserire sul database una nuova nazione",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode="201", description="Nazione inserita con successo sul database", content = @Content(schema = @Schema(implementation = CountryResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, code o currency non è quello atteso", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Codice o nome nazione già presenti sul database", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping // http://localhost:8081/api/countries
    public ResponseEntity<CountryResponse> save(@RequestBody @Valid CountryRequest req){
        return new ResponseEntity<>(countryService.save(req), HttpStatus.CREATED);
    }

    @Operation(
            summary = "ACTIVE COUNTRY LIST",
            description = "Questo metodo serve a visualizzare la lista delle nazioni attive",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elenco delle nazioni attive sul database",content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryResponse.class)))),
                    @ApiResponse(responseCode="404", description="Nessuna nazione attiva al momento", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/active") // http://localhost:8081/api/countries/active
    public ResponseEntity<List<CountryResponse>> getActiveCountries(){
        List<CountryResponse> list = countryService.getActiveCountries();
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Operation(
            summary = "COUNTRY LIST",
            description = "Questo metodo serve a visualizzare la lista delle nazioni attive e non",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elenco di tutte le nazioni sul database",content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryResponse.class)))),
                    @ApiResponse(responseCode="404", description="Nessuna nazione al momento", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping // http://localhost:8081/api/countries
    public ResponseEntity<List<CountryResponse>> getCountries(){
        List<CountryResponse> list = countryService.getCountries();
        if(list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Operation(
            summary = "COUNTRY UPDATE",
            description = "Questo metodo serve a modificare una nazione",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nazione modificate con successo",content = @Content(schema = @Schema(implementation = CountryResponse.class))),
                    @ApiResponse(responseCode="404", description="Nessuna nazione trovata con questo id", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="400", description="Parametro non valido id <= 0", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Codice o nome nazione già presenti sul database", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}") // http://localhost:8081/api/countries/{id}
    public ResponseEntity<CountryResponse> update(
            @PathVariable @Min(value = 1, message = "id deve essere un numero intero maggiore di 0") short id,
                                                  @RequestBody @Valid CountryRequest req){
        return new ResponseEntity<CountryResponse>(countryService.update(id, req), HttpStatus.OK);
    }

    @Operation(
            summary = "SWITCH ACTIVE STATE",
            description = "Questo metodo serve a modificare lo stato di una nazione",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Stato della nazione modificato con successo"),
                    @ApiResponse(responseCode="404", description="Nessuna nazione trovata con questo id", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="400", description="Parametro non valido id <= 0", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PatchMapping("/{id}") // http://localhost:8081/api/countries/{id}
    public ResponseEntity<Void> switchCountryStatus(
            @PathVariable @Min(value = 1, message = "id deve essere un numero intero maggiore di 0") short id
    ){
        countryService.switchCountryStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "GET COUNTRY BY ID",
            description = "Questo metodo serve a visualizzare la nazione selezionata con l'id",
            tags = {"Country"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nazione trovata",content = @Content(schema = @Schema(implementation = CountryResponse.class))),
                    @ApiResponse(responseCode="404", description="Nessuna nazione trovata con questo id", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="400", description="Parametro non valido id <= 0", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/{id}") // http://localhost:8081/api/countries/{id}
    public ResponseEntity<CountryResponse> getCountry(
            @PathVariable @Min(value = 1, message = "id deve essere un numero intero maggiore di 0") short id
    ){
        return new ResponseEntity<>(countryService.getCountry(id), HttpStatus.OK);
    }

}