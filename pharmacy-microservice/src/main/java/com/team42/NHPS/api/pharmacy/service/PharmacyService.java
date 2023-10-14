package com.team42.NHPS.api.pharmacy.service;

import com.team42.NHPS.api.pharmacy.shared.PharmacyDto;

public interface PharmacyService {
    PharmacyDto getPharmacy(String pharmacyId);

    PharmacyDto createPharmacy(PharmacyDto pharmacyDto);
}
