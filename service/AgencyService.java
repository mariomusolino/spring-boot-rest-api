package com.odissey.tour.service;

import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.model.dto.request.AgencyRequest;
import com.odissey.tour.model.dto.response.AgencyResponse;
import com.odissey.tour.model.entity.Agency;
import com.odissey.tour.model.entity.Country;
import com.odissey.tour.repository.AgencyRepository;
import com.odissey.tour.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final CountryRepository countryRepository;

    public AgencyResponse save(AgencyRequest req){
        // verificare l'esistenza della country e istanziarne un oggetto
        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id " + req.getCountryId()));
        if(agencyRepository.existsByVat(req.getVat()))
            throw new Exception409("Un agenzia con lo stesso VAT è già presente a sistema");
        // Istanzio oggetto Agency
        Agency agency = new Agency(
                req.getName().trim(),
                req.getCity().trim(),
                req.getAddress().trim(),
                req.getVat().trim(),
                country
        );
        // persisto su db la nuova agenzia
        agencyRepository.save(agency);
        // restituisco la response
        return AgencyResponse.fromEntityToDto(agency);
    }


    public List<AgencyResponse> findAllAgencies(){
        List<AgencyResponse> list = agencyRepository.findAllAgencies();
        if(list.isEmpty())
            throw new Exception404("Nessuna agenzia trovata. ");
        return list;
    }

    public AgencyResponse getAgency(int id){
        return agencyRepository.findAgency(id)
                .orElseThrow(()-> new Exception404("Nessuna agenzia trovata con id "+id));
    }


    public List<AgencyResponse> getAgenciesByCountry(short countryId){
        List<AgencyResponse> list = agencyRepository.findAgenciesByCountry(countryId);
        if(list.isEmpty())
            throw new Exception404("Nessuna agenzia trovata per la nazione selezionata");
        return list;
    }


    public AgencyResponse update(int id, AgencyRequest req){
        // verificare l'esistenza della country e istanziarne un oggetto
        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id " + req.getCountryId()));
        // verificare l'esistenza dell'agency e istanziarne un oggetto
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(()-> new Exception404("Agenzia non trovata con id " + id));
        // verificare che non esista un'altra agency con lo stesso VAT in quanto è un valore UNIQUE
        if(agencyRepository.existsByVatAndIdNot(req.getVat().trim(), id))
            throw new Exception409("Un'agenzia con lo stesso VAT è già presente a sistema");
        // settare i nuovi valori
        agency.setName(req.getName().trim());
        agency.setCity(req.getCity().trim());
        agency.setAddress(req.getAddress().trim());
        agency.setVat(req.getVat().trim());
        agency.setCountry(country);

        agencyRepository.save(agency);

        return AgencyResponse.fromEntityToDto(agency);
    }


    public void switchAgencyStatus(int id){
        // verificare l'esistenza dell'agency e istanziarne un oggetto
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(()-> new Exception404("Agenzia non trovata con id " + id));
        agency.setActive(!agency.isActive());
        agencyRepository.save(agency);
    }


}