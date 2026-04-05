package com.stockpro.product.repository;

import com.stockpro.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByCategoryAndActiveTrue(String category);

    List<Product> findByBrandAndActiveTrue(String brand);

    List<Product> findByActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Product> searchActive(@Param("q") String q);
}