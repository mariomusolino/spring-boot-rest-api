package com.odissey.tour.controller;

import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.model.dto.response.CustomerResponse;
import com.odissey.tour.model.dto.response.RatingResponse;
import com.odissey.tour.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Operation(
            summary = "ADD/UPDATE RATE",
            description = "Questo metodo consete ad un customer di dare o aggiornare un voto ad un tour.",
            tags = {"Rating"},
            responses = {
                    @ApiResponse(responseCode="201", description="Voto inserito/aggiornato con successo.", content = @Content(schema = @Schema(implementation = RatingResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di tourId o rate non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Tour non trovato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping("/{tourId}/{rate}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<RatingResponse> create(
            @PathVariable @Min(value = 1, message = "L'id del tour deve essere un numero intero positivo") int tourId,
            @PathVariable @Min(value = 1, message = "Il voto non può essere inferiore a 1") @Max(value = 5, message = "Il voto non può essere maggiore di 5") int rate,
            @AuthenticationPrincipal UserDetails userDetails
            ){
        return new ResponseEntity<>(ratingService.create(tourId, rate, userDetails), HttpStatus.CREATED);
    }
}
