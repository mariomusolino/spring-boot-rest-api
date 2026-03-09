package com.odissey.tour.controller;

import com.odissey.tour.model.dto.request.ChangePasswordRequest;
import com.odissey.tour.model.dto.request.UserRequest;
import com.odissey.tour.model.dto.request.UserRoleRequest;
import com.odissey.tour.model.dto.request.UserUpdateRequest;
import com.odissey.tour.model.dto.response.AgencyResponse;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.model.dto.response.UserDetailResponse;
import com.odissey.tour.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "REGISTER ADMIN/OPERATOR USER TYPE",
            description = "Questo metodo serve a inserire un nuovo utente di tipo ADMIN o OPERATOR sul database",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode="201", description="Utente inserito con successo sul database.", content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di username, email, password, firstname, lastname oppure role non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Username o email già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="500", description="Si è verificato un errore durante l'invio dell'email.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserDetailResponse> register(@RequestBody @Valid UserRoleRequest req){
        return new ResponseEntity<>(userService.register(req),HttpStatus.CREATED);
    }


    @Operation(
            summary = "RESEND OTP CODE",
            description = "Questo metodo serve reinviare il codice otp all'utente.",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode="200", description="Codice inviato correttamente all'utente", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="Il valore di id non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Utente non trovato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="500", description="Si è verificato un errore durante l'invio dell'email.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<String> resendOtpCode(
            @PathVariable @Min(value = 1, message = "L'id dell'utente deve essere un numero intero maggiore di zero") int id
    ){
        return new ResponseEntity<>(userService.resendOtpCode(id), HttpStatus.OK);
    }


    @Operation(
            summary = "ENABLE/DISABLE USER",
            description = "Questo metodo serve abilitare o disabilitare un utente.",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode="200", description="Abilitazione/disabilitazione avvenuta con successo", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="404", description="Utente non trovato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/{id}/enabling")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> enableDisableUser(
            @PathVariable @Min(value = 1, message = "L'id dell'utente deve essere un numero intero maggiore di zero") int id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return new ResponseEntity<>(userService.enableDisableUser(id, userDetails), HttpStatus.OK);
    }


    @Operation(
            summary = "UPDATE USER",
            description = "Questo metodo consete ad un utente (autenticato) di aggiornare le proprie informazioni realtive a username, email, firstname e lastname.",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode="200", description="Aggiornamento avvenuto con successo", content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di username, email, firstname oppure lastname non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Username o email già presenti sul database.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PutMapping
    public ResponseEntity<UserDetailResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UserUpdateRequest req
    ){
        return new ResponseEntity<>(userService.update(userDetails, req), HttpStatus.OK);
    }

    @Operation(
            summary = "RETURN THE LAST LOGIN BY USER",
            description = "Questo metodo restituisce la data e l'ora dell'ultimo login dell'utente",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode="200", description="Ultimo login restituito con successo", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="Il valore di user id non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="L'utente non si è mai loggato", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @GetMapping("/last_login/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> lastLoginByUser(
            @Parameter(required = true, description = "L'id dell'utente deve essere un numero intero maggiore di zero")
            @PathVariable @Min(value = 1, message = "L'id dell'utente deve essere un numero intero maggiore di zero") int userId
    ){
        return new ResponseEntity<>(userService.lastLoginByUser(userId), HttpStatus.OK);
    }

    @Operation(
            summary = "CHANGE PASSWORD",
            description = "Questo metodo permette all'utente di cambiare la propria password in autonomia",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode="200", description="Cambio password avvenuto con successo", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="I valori della vecchia e/o nuova password non sono quelli attesi.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="La password scelta è già stata utilizzata in uno dei tre precedenti cambi password", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping("/change_password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequest req
    ){
        return  new ResponseEntity<>(userService.changePassword(userDetails, req), HttpStatus.OK);
    }




}
