package com.team42.NHPS.api.pharmacy.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRespository extends CrudRepository<InventoryEntity, String> {
    Optional<InventoryEntity> findByPharmacyMedicationKey_PharmacyIdAndPharmacyMedicationKey_MedicationId(String pharmacyId, String medicationId);
}
