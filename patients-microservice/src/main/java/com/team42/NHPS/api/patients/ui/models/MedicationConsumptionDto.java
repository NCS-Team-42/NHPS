package com.team42.NHPS.api.patients.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationConsumptionDto { // to be used by inventory service to adjust pharmacy stock level
    String pharmacyId;
    List<MedicationQuantityConsumed> medicationQuantityConsumedList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class MedicationQuantityConsumed {
        private String medicationId;
        private int quantity;
    }
}
