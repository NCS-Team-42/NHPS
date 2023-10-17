package com.team42.NHPS.api.pharmacy.shared;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    @NotNull
    private String pharmacyId;
    @NotNull
    private String medicineId;
    @NotNull
    private int quantity; // Admin set
    @NotNull
    private int velocityInWeekly; // Admin set
    @Nullable
    private int velocityOutWeekly; // Sum from patient_medicine mapping
}
