package com.odissey.tour.service;

import com.odissey.tour.exception.Exception400;
import com.odissey.tour.exception.Exception401;
import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception500;
import com.odissey.tour.model.dto.request.TourGeneratorRequest;
import com.odissey.tour.model.dto.response.CustomErrorResponse;
import com.odissey.tour.model.dto.response.TourDetailResponse;
import com.odissey.tour.model.dto.response.TourGeneratorResponse;
import com.odissey.tour.model.entity.Branch;
import com.odissey.tour.model.entity.Country;
import com.odissey.tour.model.entity.Tour;
import com.odissey.tour.repository.BranchRepository;
import com.odissey.tour.repository.CountryRepository;
import com.odissey.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourGeneratorService {

    private final BranchRepository branchRepository;
    private final CountryRepository countryRepository;
    private final TourRepository tourRepository;
    private final RestTemplate restTemplate;

    private final static String URI = "http://localhost:8090/api/generate";

    public TourDetailResponse generateTour(int branchId, TourGeneratorRequest req){
        try {
            Branch branch = branchRepository.findByIdAndActiveTrue(branchId)
                    .orElseThrow(() -> new Exception404("Branch non trovato o non più attivo"));

            String apiKey = branch.getApiKey();
            if (apiKey == null)
                throw new Exception401("La filiale non ha a disposizione una api key per contattare il servizio di AI");

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<TourGeneratorRequest> entity = new HttpEntity<>(req, headers);
            ResponseEntity<TourGeneratorResponse> response =
                    restTemplate.exchange(
                            URI,
                            HttpMethod.POST,
                            entity,
                            TourGeneratorResponse.class
                    );
/*
        if(response.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
            throw new Exception400("Api Key errata o scaduta");

        if(!response.getStatusCode().equals(HttpStatus.CREATED))
            throw new Exception500("Qualcosa è andato storto nella generazione del tour da parte di AI");
*/
            TourGeneratorResponse tourGeneratorResponse = response.getBody();

            Country country = countryRepository.findByCodeAndActiveTrue(tourGeneratorResponse.getCountryCode())
                    .orElseThrow(() -> new Exception404("Country non trovata con codice " + tourGeneratorResponse.getCountryCode()));

            Tour tour = new Tour(
                    branch,
                    country,
                    tourGeneratorResponse.getTitle(),
                    tourGeneratorResponse.getDescription(),
                    tourGeneratorResponse.getStartDate(),
                    tourGeneratorResponse.getEndDate(),
                    tourGeneratorResponse.getMinPax(),
                    tourGeneratorResponse.getMaxPax(),
                    tourGeneratorResponse.getPrice()
            );
            tourRepository.save(tour);
            return TourDetailResponse.fromEntityToDto(tour);
        } catch (HttpClientErrorException.Unauthorized e){
            throw new Exception401("Token non valido o scaduto: filiale non autorizzata all'uso del servizio di AI");
        }
    }
}