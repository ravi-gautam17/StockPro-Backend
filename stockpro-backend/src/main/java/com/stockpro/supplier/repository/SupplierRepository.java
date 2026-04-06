package com.stockpro.supplier.repository;

import com.stockpro.supplier.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByActiveTrue();

    List<Supplier> findByCityIgnoreCaseContainingAndActiveTrue(String city);

    List<Supplier> findByCountryIgnoreCaseContainingAndActiveTrue(String country);

    @Query("SELECT s FROM Supplier s WHERE s.active = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Supplier> searchActiveByName(@Param("q") String q);
}
