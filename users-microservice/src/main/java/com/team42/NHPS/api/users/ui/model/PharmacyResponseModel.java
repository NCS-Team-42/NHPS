package com.team42.NHPS.api.users.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyResponseModel {
    private String pharmacyId;

    private String pharmacyName;

    private String postalCode;
}