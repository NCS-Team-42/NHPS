package com.team42.NHPS.api.pharmacy.service;

import com.team42.NHPS.api.pharmacy.shared.PharmacyDto;
import com.team42.NHPS.api.pharmacy.shared.PharmacyUserMappingDto;
import com.team42.NHPS.api.pharmacy.shared.PrescriptionDto;
import com.team42.NHPS.api.pharmacy.ui.model.UpdateInventoryRequestModel;

import java.util.List;
import java.util.Map;

public interface PharmacyService {
    PharmacyDto getPharmacy(String pharmacyId);

    PharmacyDto createPharmacy(PharmacyDto pharmacyDto);

    List<PharmacyDto> getAllPharmacy();

    Map<String, String> mapPharmacyToUser(PharmacyUserMappingDto pharmacyUserMappingDto, String authorization);

    void updateInventory(UpdateInventoryRequestModel updateInventoryRequestModel);
}
