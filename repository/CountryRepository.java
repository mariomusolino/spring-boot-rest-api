package com.odissey.tour.repository;

import com.odissey.tour.model.dto.response.CountryResponse;
import com.odissey.tour.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Short> {

    boolean existsByCode(String code);
    boolean existsByName(String name);
    Optional<Country> findByIdAndActiveTrue(short id);
    Optional<Country> findByCodeAndActiveTrue(String code);
    boolean existsByCodeAndIdNot(String code, short id);
    boolean existsByNameAndIdNot(String name, short id);

    // JPQL java persistence query language (mix tra java e sql)
    // sql nativo -> SELECT id, name FROM countries ORDER BY name
    @Query("SELECT new com.odissey.tour.model.dto.response.CountryResponse(" +
            "c.id, c.name" +
            ") FROM Country c " +
            "WHERE c.active = true " +
            "ORDER BY c.name")
    List<CountryResponse> findAllActiveCountries();

    @Query("SELECT new com.odissey.tour.model.dto.response.CountryResponse(" +
            "c.id, c.name" +
            ") FROM Country c " +
            "ORDER BY c.name")
    List<CountryResponse> findAllCountries();

    @Query("SELECT new com.odissey.tour.model.dto.response.CountryResponse(" +
            "c.id, c.code, c.name, c.currency " +
            ") FROM Country c " +
            "WHERE c.id = :countryId ")  //countryId è il nome della variabile che deve essere uguale al nome settato con @Param
    Optional<CountryResponse> findCountry(@Param("countryId") short id);

    @Query("SELECT new com.odissey.tour.model.dto.response.CountryResponse(" +
            "c.id, c.code, c.name, c.currency " +
            ") FROM Country c " +
            "WHERE c.id = :countryId " +
            "AND c.active = true")  //countryId è il nome della variabile che deve essere uguale al nome settato con @Param
    Optional<CountryResponse> findActiveCountry(@Param("countryId") short id);

}
