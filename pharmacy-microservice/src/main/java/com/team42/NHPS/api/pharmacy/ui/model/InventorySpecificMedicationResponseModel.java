package com.team42.NHPS.api.pharmacy.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySpecificMedicationResponseModel {
    private int quantity;
    private int velocityInWeekly;
    private int velocityOutWeekly;
    private PharmacyResponseModel pharmacyResponseModel;
    private MedicationResponseModel medicationResponseModel;
}
