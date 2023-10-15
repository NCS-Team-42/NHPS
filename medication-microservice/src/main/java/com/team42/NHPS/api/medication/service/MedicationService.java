package com.team42.NHPS.api.medication.service;

import com.team42.NHPS.api.medication.shared.MedicationDto;
import com.team42.NHPS.api.medication.ui.model.CreateMedicationRequestModel;

import java.util.List;

public interface MedicationService {

    MedicationDto findByMedicationId(String medicationID);
    List<MedicationDto> findAll();
    MedicationDto createMedication(MedicationDto medicationDto);
}
