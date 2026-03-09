package com.odissey.tour.repository;

import com.odissey.tour.model.dto.response.TourResponse;
import com.odissey.tour.model.entity.Tour;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    @Query("SELECT new com.odissey.tour.model.dto.response.TourResponse(" +
            "t.id, " +
            "t.country.code, " +
            "t.name, " +
            "t.startDate, " +
            "t.endDate, " +
            "CAST(t.status AS STRING), " +
            "t.price)" +
            "FROM Tour t " +
            "WHERE (:branchId IS NULL OR t.branch.id = :branchId)")
    Page<TourResponse> getAllTours(Integer branchId, Pageable pageable);

    @Query("SELECT new com.odissey.tour.model.dto.response.TourResponse(" +
            "t.id, " +
            "t.country.code, " +
            "t.name, " +
            "t.startDate, " +
            "t.endDate, " +
            "CAST(t.status AS STRING), " +
            "t.price, " +
            "t.description)" +
            "FROM Tour t " +
            "WHERE (:branchId IS NULL OR t.branch.id = :branchId) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:countryId IS NULL OR t.country.id = :countryId) " +
            "AND " +
            "   (" +
            "       (:startDate IS NULL AND :endDate IS NULL) OR " +
            "       (:endDate IS NULL AND :startDate IS NOT NULL AND t.startDate >= :startDate) OR " +
            "       (:endDate IS NOT NULL AND :startDate IS NOT NULL AND t.startDate BETWEEN :startDate AND :endDate)" +
            "   ) " +
            "AND " +
            "   (" +
            "       (:maxPrice IS NOT NULL AND t.price >= :minPrice AND t.price <= :maxPrice) OR " +
            "       (:maxPrice IS NULL AND t.price >= :minPrice)" +
            "   ) " +
            "AND (:avg IS NULL OR t.avgRating = :avg) " +
            "AND (:keyword IS NULL OR t.name LIKE :keyword OR t.description LIKE :keyword)")
    List<TourResponse> getFilteredTours(Integer branchId, TourStatus status, Short countryId,
                                        LocalDate startDate, LocalDate endDate,
                                        float minPrice, Float maxPrice, Double avg, String keyword, Sort sort);

    @Query("SELECT t FROM Tour t " +
            "WHERE t.status NOT IN (:statuses)")
    List<Tour> findValidTours(Set<TourStatus> statuses);

}