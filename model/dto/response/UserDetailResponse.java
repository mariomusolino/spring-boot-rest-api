package com.odissey.tour.model.dto.response;

import com.odissey.tour.model.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserDetailResponse extends UserResponse {


    private String username;
    private String role;
    private boolean verified;
    private boolean enabled;

    public UserDetailResponse(int id, String email, String firstname, String lastname, String username, String role, boolean verified, boolean enabled) {
        super(id, email, firstname, lastname);
        this.username = username;
        this.role = role;
        this.verified = verified;
        this.enabled = enabled;
    }

    public static UserDetailResponse fromEntityToDto(User user){
        return new UserDetailResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getRole().name(),
                user.isVerified(),
                user.isEnabled()
        );
    }

}
