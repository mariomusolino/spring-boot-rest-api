package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 *  The structure of CustomErrorResponse is compliant to
 *  <a href="https://www.rfc-editor.org/rfc/rfc7807.html">rfc 7807</a>
 */

@Data
@AllArgsConstructor @NoArgsConstructor
public class CustomErrorResponse {

    private String type;
    private String title;
    private int status;
    // private String detail; // Commentata in quanto non è auspicabile che finisca nella resposne
    private String instance;
    private Map<String, String> errors;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime when = LocalDateTime.now();

    public static CustomErrorResponse getCustomErrorResponse(String type, HttpStatus httpStatus, String uri, String errorMsg){
        return new CustomErrorResponse(
                type,
                httpStatus.name(),
                httpStatus.value(),
                uri,
                Map.of("message", errorMsg),
                LocalDateTime.now()
        );
    }
}