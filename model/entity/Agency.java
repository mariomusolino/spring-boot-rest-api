package com.odissey.tour.model.entity;

import com.odissey.tour.model.entity.listeners.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "agencies")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) @ToString

public class Agency extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String vat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Country country;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = false)
    List<Branch> branches = new ArrayList<>();

    public Agency(String name, String city, String address, String vat, Country country) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.vat = vat;
        this.country = country;
        active = true;
    }
}
