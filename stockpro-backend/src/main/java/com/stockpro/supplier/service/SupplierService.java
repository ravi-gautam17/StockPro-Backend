package com.stockpro.supplier.service;

import com.stockpro.supplier.domain.Supplier;
import com.stockpro.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> listActive() {
        return supplierRepository.findByActiveTrue();
    }

    public Supplier get(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
    }

    @Transactional
    public Supplier create(Supplier s) {
        s.setId(null);
        s.setActive(true);
        if (s.getRating() == null) {
            s.setRating(BigDecimal.ZERO);
        }
        return supplierRepository.save(s);
    }

    @Transactional
    public Supplier update(Long id, Supplier patch) {
        Supplier s = get(id);
        if (patch.getName() != null) {
            s.setName(patch.getName());
        }
        if (patch.getContactPerson() != null) {
            s.setContactPerson(patch.getContactPerson());
        }
        if (patch.getEmail() != null) {
            s.setEmail(patch.getEmail());
        }
        if (patch.getPhone() != null) {
            s.setPhone(patch.getPhone());
        }
        if (patch.getAddress() != null) {
            s.setAddress(patch.getAddress());
        }
        if (patch.getCity() != null) {
            s.setCity(patch.getCity());
        }
        if (patch.getCountry() != null) {
            s.setCountry(patch.getCountry());
        }
        if (patch.getPaymentTerms() != null) {
            s.setPaymentTerms(patch.getPaymentTerms());
        }
        s.setLeadTimeDays(patch.getLeadTimeDays());
        return supplierRepository.save(s);
    }

    @Transactional
    public void deactivate(Long id) {
        Supplier s = get(id);
        s.setActive(false);
        supplierRepository.save(s);
    }

    @Transactional
    public void updateRating(Long id, BigDecimal score) {
        Supplier s = get(id);
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("5")) > 0) {
            throw new IllegalArgumentException("Score 0..5");
        }
        BigDecimal prev = s.getRating() != null ? s.getRating() : BigDecimal.ZERO;
        BigDecimal blended = prev.add(score).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        s.setRating(blended);
        supplierRepository.save(s);
    }
 

    public List<Supplier> search(String q, String city, String country) {
        if (q != null && !q.isBlank()) {
            return supplierRepository.searchActiveByName(q.trim());
        }
        if (city != null && !city.isBlank()) {
            return supplierRepository.findByCityIgnoreCaseContainingAndActiveTrue(city.trim());
        }
        if (country != null && !country.isBlank()) {
            return supplierRepository.findByCountryIgnoreCaseContainingAndActiveTrue(country.trim());
        }
        return listActive();
    }
}

