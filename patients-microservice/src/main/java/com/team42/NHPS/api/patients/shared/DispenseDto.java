package com.team42.NHPS.api.patients.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispenseDto {
    @NonNull
    private String patientNric;
    @NonNull
    private String medicationId;
    private int quantity;
}
