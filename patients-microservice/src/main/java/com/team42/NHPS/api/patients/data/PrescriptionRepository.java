package com.team42.NHPS.api.patients.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends CrudRepository<PrescriptionEntity, String> {

    List<PrescriptionEntity> findByPatientMedicationKey_PatientNric(String nric);

//    @Procedure(procedureName = "batch_process_consumption")
//    void batchProcessConsumption(String nric);

    Optional<PrescriptionEntity> findByPatientMedicationKey_PatientNricAndPatientMedicationKey_MedicationId(String patientNric, String medicationId);

    @Query("select sum(p.consumptionWeekly) from PrescriptionEntity p where p.pharmacyId = :pharmacyId and p.patientMedicationKey.medicationId = :medicationId")
    Integer getSumByPharmacyIdAndMedicationId(@Param("pharmacyId") String pharmacyId, @Param("medicationId") String medicationId);
}
