package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import com.team42.NHPS.api.patients.ui.models.MedicationResponseModel;
import com.team42.NHPS.api.patients.ui.models.PharmacyResponseModel;

import java.util.List;

public interface PrescriptionService {

    PrescriptionDto createPrescription(PrescriptionDto prescriptionDto, String authorization);

    List<PrescriptionDto> getPrescriptionByNric(String nric);

    MedicationResponseModel medicationCheck(String medicationId, String authorization);

    PharmacyResponseModel pharmacyCheck(String pharmacyId, String authorization);

    String batchProcessConsumption(String nric, String authorization);
}
