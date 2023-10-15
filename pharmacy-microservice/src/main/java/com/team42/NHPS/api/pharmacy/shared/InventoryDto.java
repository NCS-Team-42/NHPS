package com.team42.NHPS.api.pharmacy.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    private String pharmacyId;
    private String medicineId;
    private int quantity;
    private int velocityInWeekly; // Admin set
    private int velocityOutWeekly; // Sum from patient_medicine mapping
}
