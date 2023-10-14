package com.team42.NHPS.api.pharmacy.data;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PharmacyRepository extends CrudRepository<PharmacyEntity, Long> {
    Optional<PharmacyEntity> findByPharmacyId(String pharmacyId);
}
