package com.odissey.tour.model.entity;

import com.odissey.tour.model.entity.listeners.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "countries")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) @ToString
public class Country extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Short id;

    @Column(length = 2, nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true) // se non imposto la lunghezza il rispettivo campo sul db prenderà la lunghezza massima per il tipo
    private String name;

    @Column(nullable = false)
    private String currency;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Country(String code, String name, String currency) {
        this.code = code;
        this.name = name;
        this.currency = currency;
        this.active = true;
    }
}
