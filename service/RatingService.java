package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception404;
import com.odissey.tour.model.dto.response.RatingResponse;
import com.odissey.tour.model.entity.*;
import com.odissey.tour.model.entity.enumerator.TourStatus;
import com.odissey.tour.repository.CustomerRepository;
import com.odissey.tour.repository.RatingRepository;
import com.odissey.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {

    private final RatingRepository ratingRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public RatingResponse create(int tourId, int rate, UserDetails userDetails) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(()-> new Exception404("Tour non trovato con id: " + tourId));
        if(!tour.getStatus().equals(TourStatus.EXPIRED))
            throw new Exception400("Solo un tour terminato può essere votato");

        User user = (User) userDetails;
        Customer customer = customerRepository.findById(user.getId())
                .orElseThrow(()-> new Exception404("Customer non trovato con id: " + user.getId()));
        if(!tour.getCustomers().contains(customer))
            throw new Exception400("Solo chi ha partecipato al tour può votare");

        Rating rating = new Rating(new RatingId(customer, tour), rate);
        ratingRepository.save(rating);

        double avg = ratingRepository.calcAvgByTour(tourId);
        tour.setAvgRating(avg);

        return new RatingResponse(tourId, rate, avg);
    }
}
