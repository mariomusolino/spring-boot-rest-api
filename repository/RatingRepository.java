package com.odissey.tour.repository;

import com.odissey.tour.model.entity.Rating;
import com.odissey.tour.model.entity.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {

    @Query("SELECT COALESCE(AVG(r.rate), 0d) FROM Rating r WHERE r.ratingId.tour.id = :tourId")
    double calcAvgByTour(int tourId);
}
