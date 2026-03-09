package com.odissey.tour.model.entity;

import com.odissey.tour.model.entity.enumerator.PaymentType;
import com.odissey.tour.model.entity.listeners.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Payment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Voucher voucher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Column(nullable = false)
    private float amount;

    public Payment(Tour tour, Customer customer, Voucher voucher, PaymentType type, float amount) {
        this.tour = tour;
        this.customer = customer;
        this.voucher = voucher;
        this.type = type;
        this.amount = amount;
    }
}
