package com.odissey.tour.repository;

import com.odissey.tour.model.dto.response.BranchResponse;
import com.odissey.tour.model.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Integer> {

    boolean existsByNameAndAgencyId(String name, int agencyId);
    boolean existsByVat(String vat);
    boolean existsByVatAndIdNot(String vat, int id);
    boolean existsByNameAndAgencyIdAndIdNot(String name, int agencyId, int branchId);
    Optional<Branch> findByIdAndActiveTrue(int branchId);
    boolean existsByApiKeyAndIdNot(String apiKey, int branchId);

    @Query("SELECT new com.odissey.tour.model.dto.response.BranchResponse(" +
            "b.id, " +
            "b.name, " +
            "b.city, " +
            "b.address, " +
            "b.vat," +
            "b.agency.country.name" +
            ") FROM Branch b " +
            "WHERE b.agency.id = :agencyId " +
            "AND b.agency.active = true " +
            "AND b.active = true " +
            "ORDER BY b.name")
    List<BranchResponse> getBranchesByAgency(int agencyId);

    @Query("SELECT new com.odissey.tour.model.dto.response.BranchResponse(" +
            "b.id, " +
            "b.name, " +
            "b.city, " +
            "b.address, " +
            "b.vat," +
            "b.agency.id, " +
            "b.agency.name" +
            ") FROM Branch b " +
            "WHERE b.agency.country.id = :countryId " +
            "AND b.agency.country.active = true " +
            "AND b.active = true " +
            "ORDER BY b.name")
    List<BranchResponse> getBranchesByCountry(short countryId);

    @Query("SELECT new com.odissey.tour.model.dto.response.BranchResponse(" +
            "b.id, " +
            "b.name, " +
            "b.city, " +
            "b.address, " +
            "b.vat," +
            "a.id, " +
            "a.name, " +
            "c.name" +
            ") FROM Branch b " +
            "INNER JOIN Agency a ON a.id = b.agency.id " +
            "INNER JOIN Country c ON c.id = a.country.id " +
            "WHERE b.id = :id " +
            "AND b.active = true")
    Optional<BranchResponse> getBranch(int id);

    /*
    @Query("SELECT c.currency FROM Branch b " +
            "INNER JOIN Agency a ON a.id = b.agency.id " +
            "INNER JOIN Country c ON c.id = a.country.id " +
            "WHERE b.id = :id")
    String getBranchCurrency(int id);
    */


}
