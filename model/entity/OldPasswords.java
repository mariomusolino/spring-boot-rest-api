package com.odissey.tour.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OldPasswords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String oldPassword;

    @Column(nullable = false)
    private LocalDateTime lastChangePassword;

    public OldPasswords(User user, String oldPassword, LocalDateTime lastChangePassword) {
        this.user = user;
        this.oldPassword = oldPassword;
        this.lastChangePassword = lastChangePassword;
    }
}
