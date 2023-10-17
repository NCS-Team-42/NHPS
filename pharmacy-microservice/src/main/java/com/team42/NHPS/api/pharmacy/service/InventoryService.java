package com.team42.NHPS.api.pharmacy.service;

import com.team42.NHPS.api.pharmacy.shared.InventoryDto;

import java.util.List;

public interface InventoryService {
    void synchronizeWeeklyConsumption(String authorization);

    InventoryDto findByPharmacyIdAndMedicationId(String pharmacyId, String medicationId);

    List<InventoryDto> findByPharmacyId(String pharmacyId);
}
