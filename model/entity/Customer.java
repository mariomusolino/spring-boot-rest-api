package com.odissey.tour.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {

    // 1. Viene definito un ID per l'entità
    @Id
    @EqualsAndHashCode.Include
    private Integer id;

    // 2. Mappatura della relazione @OneToOne
    // L'attributo 'optional = false' è una buona pratica qui, indicando che
    // ogni Customer deve avere uno User (nonostante la FK sia la PK).
    @OneToOne(optional = false)
    // 3. LA CHIAVE: @MapsId indica che il valore di 'id' (sopra)
    // sarà preso dalla chiave primaria del campo 'user'.
    // In pratica, l'id di Customer è la FK che punta a User.id.
    // Si tratta quindi di una Shared Primary Key (Chiave primaria condivisa)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Country country;

    @ManyToMany(mappedBy = "customers")
    private Set<Tour> tours = new HashSet<>();

    /* SINCRONIZZAZIONE *
     * Setter necessario per la sincronizzazione sul lato User
     */
    public void setUser(User user) {
        this.user = user;
        // Quando lo User viene impostato, viene sincronizzato l'Id
        if (user != null) {
            this.id = user.getId();
        }
    }

    public Customer(Integer id) {
        this.id = id;
    }
}