package com.team42.NHPS.api.patients.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseModel {
    private String nric;
    private int consumptionWeekly;
    private int doseLeft;
    private PharmacyResponseModel pharmacyResponseModel;
    private  MedicationResponseModel medicationResponseModel;
}
