package com.odissey.tour.model.entity;

import com.odissey.tour.model.entity.listeners.AuditableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "ratings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Rating extends AuditableEntity {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private RatingId ratingId;

    private int rate;
}
