package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.TourRequest;
import com.odissey.tour.model.dto.response.*;
import com.odissey.tour.service.FileService;
import com.odissey.tour.service.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
@Validated
public class TourController {

    private final TourService tourService;
    private final FileService fileService;

    @Operation(
            summary = "CREATE TOUR",
            description = "Questo metodo serve a inserire una nuovo tour sul database",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour creato con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di name, description, country, branch, startDate, endDate, minPax, maxPax o price non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Filiale o Nazione di riferimento non trovata o non attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<TourDetailResponse> create(@RequestBody @Valid TourRequest req){
        return new ResponseEntity<>(tourService.create(req), HttpStatus.CREATED);
    }


    @Operation(
            summary = "GET PAGINATED TOURS ",
            description = "Questo metodo restituisce tutti i tour presenti a database impaginandoli.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco tour generato con successo.", content = @Content(schema = @Schema(implementation = TourResponsePaginated.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di branchId, pageNumber, pageSize, sortBy o direction non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Nessun tour trovato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public ResponseEntity<TourResponsePaginated> getAllTours(
            @RequestParam(required = false) @Min(value = 1, message = "L'id della filiale deve essere un numero intero positivo maggiore di zero.") Integer branchId,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Il numero di elementi per pagina deve essere un un numero intero positivo maggiore di zero.") int pageSize,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Il numero della pagina da cui partire deve essere un numero intero positivo o zero (prima pagina)") int pageNumber,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ){
        TourResponsePaginated page = tourService.getAllTours(branchId, pageSize, pageNumber, sortBy, direction);
        if(page.getData().isEmpty())
            return new ResponseEntity<>(page, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Operation(
            summary = "GET FILTERED PAGINATED TOURS ",
            description = "Questo metodo restituisce tutti i tour presenti a database impaginandoli in base ai filtri impostati.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="200", description="Elenco tour generato con successo.", content = @Content(schema = @Schema(implementation = TourResponsePaginated.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di branchId, status, pageNumber, pageSize, sortBy o direction non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Nessun tour trovato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/filtered")
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'ADMIN')")
    public ResponseEntity<TourResponsePaginated> getFilteredTours(
            @RequestParam(required = false) @Min(value = 1, message = "L'id della filiale deve essere un numero intero positivo maggiore di zero.") Integer branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Short countryId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0.0") @PositiveOrZero(message = "Il prezzo minimo non può essere negativo.") float minPrice,
            @RequestParam(required = false) Float maxPrice,
            @Parameter(description = "Se valorizzato a zero restituisce i tour che non hanno mai ricevuto un rate, al netto degli altri fitri.")
            @RequestParam(required = false) @PositiveOrZero(message = "Il valore della media non può essere negativo") Double avg,
            @Parameter(description = "Se valorizzato restituisce tutti i tour che contengono nel titolo o nella descrizione la parola chiave.")
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean isCaseSensitive,
            @RequestParam(defaultValue = "false") boolean isExactMatch,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Il numero di elementi per pagina deve essere un un numero intero positivo maggiore di zero.") int pageSize,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Il numero della pagina da cui partire deve essere un numero intero positivo o zero (prima pagina)") int pageNumber,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ){
        TourResponsePaginated page = tourService.getFilteredTours(branchId, status, countryId, startDate, endDate, minPrice, maxPrice, avg, keyword, isCaseSensitive, isExactMatch, pageSize, pageNumber, sortBy, direction);
        if(page.getData().isEmpty())
            return new ResponseEntity<>(page, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    @Operation(
            summary = "UPDATE TOUR",
            description = "Questo metodo serve ad aggiornare un tour. Un tour può essere modificato finché è in stato WORK_IN_PROGRESS.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour modificato con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di id, name, description, country, branch, startDate, endDate, minPax, maxPax o price non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Filiale o Nazione di riferimento non trovata o non attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<TourDetailResponse> update(
            @PathVariable @Min(value = 1, message = "L'id del tour deve essere un un numero intero positivo maggiore di zero.") int id,
            @RequestBody @Valid TourRequest req
    ){
        return new ResponseEntity<>(tourService.update(id, req), HttpStatus.OK);
    }


    @Operation(
            summary = "CANCEL TOUR",
            description = "Questo metodo serve per cancellare un tour d'ufficio. Azione permessa solo all'ADMIN. \n" +
                    "Prevede l'invio di una notifica via mail agli eventuali partecipanti qualora il tour non fosse più in status WORK_IN_PROGRESS. \n" +
                    "Se il tour è in stato EXPIRED non può essere cancellato.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour status modificato con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Il valore del tour id non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Tour o Nazione di riferimento non trovata o non attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{id}/canceled")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<TourDetailResponse> cancelTour(
            @PathVariable @Min(value = 1, message = "L'id del tour deve essere un un numero intero positivo maggiore di zero.") int id
    ){
        return new ResponseEntity<>(tourService.cancelTour(id), HttpStatus.OK);
    }

    @Operation(
            summary = "CHANGE TOUR STATUS",
            description = "Questo metodo serve per modificare lo stato di un tour. \n" +
                    "Prevede l'invio di una notifica via mail agli eventuali partecipanti qualora il tour non fosse più in status WORK_IN_PROGRESS. \n" +
                    "Se il tour è in stato EXPIRED non può essere cancellato.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour status modificato con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Il valore del tour id o dello status non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Tour o Nazione di riferimento non trovata o non attiva", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{id}/{status}")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    ResponseEntity<TourDetailResponse> changeTourStatus(
            @PathVariable @Min(value = 1, message = "L'id del tour deve essere un un numero intero positivo maggiore di zero.") int id,
            @PathVariable @NotBlank (message = "Il nuovo status è obbligatorio") String status
    ){
        return new ResponseEntity<>(tourService.changeTourStatus(id, status), HttpStatus.OK);
    }

    @Operation(
            summary = "EARLY CANCELLATION",
            description = "Questo metodo serve a disdire la partecipazione di un customer ad un tour precedentemente prenotato.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="200", description="Tour disdetto con successo.", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di customerId o tourId non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Tour o customer non trovati.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PutMapping("/{tourId}/{customerId}")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<String> earlyCancellation(
            @PathVariable @Min(value = 1, message = "L'id del customer è obbligatorio e deve essere un numero intero positivo") int customerId,
            @PathVariable @Min(value = 1, message = "L'id del tour è obbligatorio e deve essere un numero intero positivo") int tourId
    ){
        return new ResponseEntity<>(tourService.earlyCancellation(tourId, customerId), HttpStatus.OK);
    }

    @Operation(
            summary = "UPLOAD IMAGE",
            description = "Questo metodo serve a caricare un imagine per il tour in oggetto.",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="201", description="Immagine caricata con successo con successo.", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di dimensioni(in pixel), peso o estensione non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Tour non trovato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="500", description="Caricamento file non riuscito.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping(value = "/{tourId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<TourDetailResponse> uploadImage(
            @PathVariable @Min(value = 1, message = "L'id del tour è obbligatorio e deve essere un numero intero positivo") int tourId,
            @RequestPart MultipartFile file
            ){
        return new ResponseEntity<>(fileService.uploadImage(tourId, file), HttpStatus.CREATED);
    }

}
