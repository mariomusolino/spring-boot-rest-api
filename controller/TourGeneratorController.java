package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.TourGeneratorRequest;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.model.dto.response.RatingResponse;
import com.odissey.tour.model.dto.response.TourDetailResponse;
import com.odissey.tour.service.TourGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class TourGeneratorController {

    private final TourGeneratorService tourGeneratorService;

    @Operation(
            summary = "AI TOUR GENERATION",
            description = "Questo metodo serve richimare un servizio esterno di AI che genera un tour sulla base di prompt predefinito.",
            tags = {"Tour AI"},
            responses = {
                    @ApiResponse(responseCode="201", description="Tour generato correttamente", content = @Content(schema = @Schema(implementation = TourDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di branchId o destinazione, durata, budget o type non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="403", description="Api Key errata o scaduta", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Filiale di riferimento non trovato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping("/{branchId}")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<TourDetailResponse> generateTour(
            @PathVariable @Min(value = 1, message = "Il valore dell'id della filiale deve essere intero positivo") int branchId,
            @RequestBody @Valid TourGeneratorRequest req
            ){
        return new ResponseEntity<>(tourGeneratorService.generateTour(branchId, req), HttpStatus.CREATED);
    }
}
