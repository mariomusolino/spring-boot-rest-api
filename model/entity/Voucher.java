package com.odissey.tour.model.entity;

import com.odissey.tour.model.entity.enumerator.VoucherType;
import com.odissey.tour.model.entity.listeners.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vouchers")
@Getter @Setter
@NoArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Voucher extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private LocalDate endValidity; // 1 anno a partire dalla data di emissione

    @Column(name = "is_used", nullable = false)
    private boolean used;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherType type;

    @Column(nullable = false)
    private String emittedBy;

    public Voucher(Customer customer, float price, VoucherType type, String emittedBy) {
        this.customer = customer;
        this.price = price;
        this.type = type;
        this.emittedBy = emittedBy;
        this.endValidity = LocalDate.now().plusYears(1L);
        this.used = false;
        this.code = UUID.randomUUID().toString();
    }
}
