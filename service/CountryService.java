package com.odissey.tour.service;

import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.model.dto.request.CountryRequest;
import com.odissey.tour.model.dto.response.CountryResponse;
import com.odissey.tour.model.entity.Country;
import com.odissey.tour.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final CountryRepository countryRepository;

    private static final String CACHE_COUNTRIES_ACTIVES = "countries:active";
    private static final String CACHE_COUNTRIES_ALL = "countries:all";
    private static final String CACHE_COUNTRY_DETAIL = "country:detail";

    @Caching(
            evict = {
                @CacheEvict(cacheNames = CACHE_COUNTRIES_ALL, allEntries = true),
                @CacheEvict(cacheNames = CACHE_COUNTRIES_ACTIVES, allEntries = true)
            }
    )
    public CountryResponse save(CountryRequest req) {
        String code = req.getCode().toUpperCase().trim();
        String name = req.getName().trim().substring(0, 1).toUpperCase() + req.getName().trim().substring(1, req.getName().length());
        // verificare che non esista già una country con code oppure name passati nella request
        if (countryRepository.existsByCode(code)) {
            throw new Exception409("Una nazione con codice " + code + " è già presente");
        }
        if (countryRepository.existsByName(name)) {
            throw new Exception409("Una nazione con nome " + name + " è già presente");
        }
        // istanzio oggetto Country
        Country country = new Country(code, name, req.getCurrency().trim());
        // persisto su db oggetto Country
        countryRepository.save(country);
        // mi faccio restituire una CountryResponse
        return CountryResponse.fromEntityToDto(country);
    }

    @Cacheable(cacheNames = CACHE_COUNTRIES_ACTIVES)
    public List<CountryResponse> getActiveCountries(){
        log.info(">>> Elenco di tutte le nazioni attive");
        return countryRepository.findAllActiveCountries();
    }

    @Cacheable(cacheNames = CACHE_COUNTRIES_ALL)
    public List<CountryResponse> getCountries(){
        log.info(">>> Elenco di tutte le nazioni");
        return countryRepository.findAllCountries();
    }

    @Transactional
    @Caching(
            put = @CachePut(cacheNames = CACHE_COUNTRY_DETAIL, key = "#id"),
            evict = {
                @CacheEvict(cacheNames = CACHE_COUNTRIES_ALL, allEntries = true),
                @CacheEvict(cacheNames = CACHE_COUNTRIES_ACTIVES, allEntries = true)
            }
    )
    public CountryResponse update(short id, CountryRequest req){
        // query per recuperare la nazione da aggiornare in base all'id
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new Exception404("Nazione con id " + id + " non trovata"));
        // setto i nuovi valori
        String code = req.getCode().toUpperCase().trim();
        String name = req.getName().trim().substring(0, 1).toUpperCase() + req.getName().trim().substring(1, req.getName().length());
        if(countryRepository.existsByCodeAndIdNot(code, id))
            throw new Exception409("Una nazione con il codice " + code + " esiste già");
        if(countryRepository.existsByNameAndIdNot(name, id))
            throw new Exception409("Una nazione con il nome " + name + " esiste già");
        country.setCode(code);
        country.setName(name);
        country.setCurrency(req.getCurrency());
        // il salvataggio sul db viene fatto in automatico tramite l'annotazione @Transactional
        return CountryResponse.fromEntityToDto(country);
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(cacheNames = CACHE_COUNTRIES_ALL, allEntries = true),
                @CacheEvict(cacheNames = CACHE_COUNTRIES_ACTIVES, allEntries = true),
                @CacheEvict(cacheNames = CACHE_COUNTRY_DETAIL, key = "#id")
            }
    )
    public void switchCountryStatus(short id){
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new Exception404("Nazione con id " + id + " non trovata"));
        country.setActive(!country.isActive());
    }

    @Cacheable(cacheNames = CACHE_COUNTRY_DETAIL, key = "#id")
    public CountryResponse getCountry(short id){
        return countryRepository.findCountry(id)
                .orElseThrow(() -> new Exception404("Nazione con id " + id + " non trovata"));
    }

}
