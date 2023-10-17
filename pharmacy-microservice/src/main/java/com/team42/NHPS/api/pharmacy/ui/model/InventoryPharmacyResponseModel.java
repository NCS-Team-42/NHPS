package com.team42.NHPS.api.pharmacy.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryPharmacyResponseModel {
    private PharmacyResponseModel pharmacyResponseModel;
    private List<MedicationInfo> medicationInfoList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicationInfo {
        private MedicationResponseModel medicationResponseModel;
        private int quantity;
        private int velocityInWeekly;
        private int velocityOutWeekly;
    }
}
