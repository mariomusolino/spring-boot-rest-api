package com.odissey.tour.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaginatedResponse {

    private int page;
    private int size;
    private long totalItems;
    private long totalPages;

}
