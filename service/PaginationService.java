package com.odissey.tour.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaginationService<T> {

    public static <T> Page<T> listToPage(List<T> list, Pageable pageable) {

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());

        if (start > end) {
            start = end; // evita IndexOutOfBounds in caso di pagina oltre limiti
        }

        List<T> subList = list.subList(start, end);

        return new PageImpl<>(subList, pageable, list.size());
    }
}