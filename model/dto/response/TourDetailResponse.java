package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.odissey.tour.model.entity.Country;
import com.odissey.tour.model.entity.Tour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TourDetailResponse {

    private int id;
    private String countryName;
    private String name;
    private String description;
    private String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String status;
    private int minPax;
    private int maxPax;
    private float price;
    private double avgRating;

    public static TourDetailResponse fromEntityToDto(Tour tour) {
        return new TourDetailResponse(
                tour.getId(),
                tour.getCountry().getName(),
                tour.getName(),
                tour.getDescription(),
                tour.getImage(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatus().name(),
                tour.getMinPax(),
                tour.getMaxPax(),
                tour.getPrice(),
                tour.getAvgRating()
        );
    }
}
