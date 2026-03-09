package com.odissey.tour.model.entity;

import com.odissey.tour.model.entity.listeners.AuditableEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Comment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Tour tour;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referer_to")
    private Comment refererTo; // se null il commento è riferito direttamente al tour, altrimenti è un commento in risposta ad un altro commento

    private boolean censored;

    public Comment(Customer customer, Tour tour, String content, Comment refererTo) {
        this.customer = customer;
        this.tour = tour;
        this.content = content;
        this.refererTo = refererTo;
        this.censored = false;
    }
}
