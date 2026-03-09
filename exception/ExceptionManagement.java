package com.odissey.tour.exception;

import com.odissey.tour.model.dto.response.CustomErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionManagement {

    @ExceptionHandler({Exception401.class})
    public ResponseEntity<CustomErrorResponse> unauthorizedExceptionManagement(Exception401 ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#401",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({Exception403.class})
    public ResponseEntity<CustomErrorResponse> forbiddenExceptionManagement(Exception403 ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler({Exception404.class})
    public ResponseEntity<CustomErrorResponse> notFoundExceptionManagement(Exception404 ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#404",
                HttpStatus.NOT_FOUND,
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler({Exception409.class})
    public ResponseEntity<CustomErrorResponse> conflictExceptionManagement(Exception409 ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#409",
                HttpStatus.CONFLICT,
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.CONFLICT);
    }


    @ExceptionHandler({Exception422.class})
    public ResponseEntity<CustomErrorResponse> unprocessableContentExceptionManagement(Exception422 ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#422",
                HttpStatus.UNPROCESSABLE_ENTITY,
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler({Exception400.class})
    public ResponseEntity<CustomErrorResponse> badRequestExceptionManagement(Exception400 ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#400",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({Exception500.class})
    public ResponseEntity<CustomErrorResponse> internalServerErrorExceptionManagement(Exception500 ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#500",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                ex.getMessage()
        );

        return new ResponseEntity<>(customErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Per la gestione delle BAD REQUEST generate dalle annotazioni di validazione sollevate da @Valid
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<CustomErrorResponse> argumentNotValidManagement(MethodArgumentNotValidException ex, HttpServletRequest request){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setType("https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#400");
        customErrorResponse.setTitle(HttpStatus.BAD_REQUEST.name());
        customErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error(">>> @Valid Error Details: "+ex.getMessage());
        customErrorResponse.setInstance(request.getRequestURI());
        customErrorResponse.setErrors(errors);

        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }


    // Per la gestione delle BAD REQUEST generate dalle annotazioni di validazione sollevate da @Validated
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<CustomErrorResponse> constraintViolationManagement(ConstraintViolationException ex, HttpServletRequest request){
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String path = violation.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        ConstraintViolation::getMessage));

        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setType("https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#400");
        customErrorResponse.setTitle(HttpStatus.BAD_REQUEST.name());
        customErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error(">>> @Validated Error Details: "+ex.getMessage());
        customErrorResponse.setInstance(request.getRequestURI());
        customErrorResponse.setErrors(errors);

        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<CustomErrorResponse> dateTimeParseExceptionManagement(DateTimeParseException ex, HttpServletRequest request){
        CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#400",
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                "Data inesistente oppure formato non valido per la data "+ex.getParsedString()+". Il formato corretto è yyyy-MM-dd."
        );

        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

}
