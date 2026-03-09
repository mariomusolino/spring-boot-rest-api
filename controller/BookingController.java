package com.odissey.tour.controller;

import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.model.dto.response.CustomerResponse;
import com.odissey.tour.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "BOOK A TOUR",
            description = "Questo metodo serve a prenotare un tour valido da parte di un customer",
            tags = {"Tour"},
            responses = {
                    @ApiResponse(responseCode="200", description="Prenotazione avvenuto con successo", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="L'id del tour non è valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Tour non prenotabile o customer non trovto.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Tour già prenotato in precedenza.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping("/{tourId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<String> bookingTour(
            @PathVariable @Min(value = 1, message = "L'id del tour deve essere un numero intero maggiore di zero") int tourId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return new ResponseEntity<>(bookingService.bookingTour(tourId, userDetails), HttpStatus.CREATED);
    }
}
