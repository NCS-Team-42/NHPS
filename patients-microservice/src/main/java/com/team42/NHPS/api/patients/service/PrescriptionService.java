package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.shared.DispenseDto;
import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import com.team42.NHPS.api.patients.ui.models.MedicationResponseModel;
import com.team42.NHPS.api.patients.ui.models.PharmacyResponseModel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public interface PrescriptionService {

    PrescriptionDto createPrescription(PrescriptionDto prescriptionDto, String authorization);

    List<PrescriptionDto> getPrescriptionByNric(String nric);

    MedicationResponseModel medicationCheck(String medicationId, String authorization);

    PharmacyResponseModel pharmacyCheck(String pharmacyId, String authorization);

    PrescriptionDto dispenseMedication(DispenseDto dispenseDto, String authorization);

    List<Triple<String, String, Integer>> checkSum(List<Pair<String, String>> pairList);

    List<PrescriptionDto> findAll();

//    String batchProcessConsumption(String nric, String authorization);
}
