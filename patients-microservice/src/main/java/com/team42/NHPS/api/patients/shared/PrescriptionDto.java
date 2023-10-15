package com.team42.NHPS.api.patients.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {
    private String PatientNric;
    private String MedicationId;
    private String pharmacyId;
    private int consumptionWeekly;
    private int doseLeft;
}
