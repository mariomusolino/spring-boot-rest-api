package com.odissey.tour.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRoleRequest extends UserRequest{

    @NotBlank(message = "Il ruolo è obbligatorio")
    private String role;
}
