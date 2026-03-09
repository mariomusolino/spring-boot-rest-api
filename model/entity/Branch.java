package com.odissey.tour.model.entity;

import com.odissey.tour.model.entity.listeners.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "agency_id"})})
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) @ToString
public class Branch extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Agency agency;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    private String apiKey;

    public Branch(String name, String city, String address, String vat, Agency agency) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.vat = vat;
        this.agency = agency;
        active = true;
    }

}
