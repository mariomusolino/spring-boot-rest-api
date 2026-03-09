package com.odissey.tour.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.odissey.tour.model.entity.Tour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TourResponse {

    private int id;
    private String countryCode;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String status;
    private float price;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    public TourResponse(int id, String countryCode, String name, LocalDate startDate, LocalDate endDate, String status, float price) {
        this.id = id;
        this.countryCode = countryCode;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.price = price;
    }

    public static TourResponse fromEntityToDto(Tour tour) {
        return new TourResponse(
                tour.getId(),
                tour.getCountry().getCode(),
                tour.getName(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatus().name(),
                tour.getPrice()
        );
    }
}