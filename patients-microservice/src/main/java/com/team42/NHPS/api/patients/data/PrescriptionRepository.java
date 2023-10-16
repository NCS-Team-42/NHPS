package com.team42.NHPS.api.patients.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends CrudRepository<PrescriptionEntity, String> {

    List<PrescriptionEntity> findByPatientMedicationKey_PatientNric(String nric);
}
