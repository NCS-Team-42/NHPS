package com.team42.NHPS.api.patients.ui.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationResponseModel {
    private String medicationId;
    private String name;
    private String description;
    private String activeIngredient;
    private String dosage;
    private String instructions;
}
