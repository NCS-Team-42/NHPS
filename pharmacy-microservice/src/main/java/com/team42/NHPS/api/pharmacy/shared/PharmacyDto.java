package com.team42.NHPS.api.pharmacy.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDto {
    private String pharmacyId;

    private String pharmacyName;

    private String postalCode;
}
