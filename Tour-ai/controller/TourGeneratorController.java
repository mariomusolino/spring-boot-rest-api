package com.odissey.tourai.controller;

import com.odissey.tourai.dto.TourRequest;
import com.odissey.tourai.dto.TourResponse;
import com.odissey.tourai.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generate")
@RequiredArgsConstructor
public class TourGeneratorController {

    private final TourService tourService;

    @PostMapping
    public ResponseEntity<TourResponse> generateTour(@RequestBody @Valid TourRequest req){
        return new ResponseEntity<>(tourService.generateTour(req), HttpStatus.CREATED);
    }
}
