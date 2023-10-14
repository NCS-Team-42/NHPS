package com.team42.NHPS.api.pharmacy.ui.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePharmacyRequestModel {

    @NotNull(message = "Pharmacy name cannot be null")
    private String pharmacyName;

    @NotNull(message = "Postal code cannot be null")
    private String postalCode;
}
