package com.team42.NHPS.api.patients.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {
    private String patientNric;
    private String medicationId;
    private String pharmacyId;
    private int consumptionWeekly;
    private int doseLeft = 0;
    private int prescribedDosage;

}
