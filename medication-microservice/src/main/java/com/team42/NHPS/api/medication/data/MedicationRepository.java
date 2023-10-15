package com.team42.NHPS.api.medication.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends CrudRepository<MedicationEntity, Long> {
}
