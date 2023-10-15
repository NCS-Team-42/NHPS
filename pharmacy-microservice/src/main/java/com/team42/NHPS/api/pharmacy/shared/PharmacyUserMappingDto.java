package com.team42.NHPS.api.pharmacy.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyUserMappingDto {
    private String pharmacyId;
    private String userId;
}
