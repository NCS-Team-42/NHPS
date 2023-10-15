package com.team42.NHPS.api.medication.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicationRepository extends CrudRepository<MedicationEntity, Long> {
    Optional<MedicationEntity> findByMedicationId(String medicineId);
}
