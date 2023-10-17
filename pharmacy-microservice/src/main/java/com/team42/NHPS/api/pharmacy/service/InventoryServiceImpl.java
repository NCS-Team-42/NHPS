package com.team42.NHPS.api.pharmacy.service;

import com.team42.NHPS.api.pharmacy.data.InventoryEntity;
import com.team42.NHPS.api.pharmacy.data.InventoryRespository;
import com.team42.NHPS.api.pharmacy.exception.ResourceNotFoundException;
import com.team42.NHPS.api.pharmacy.shared.InventoryDto;
import com.team42.NHPS.api.pharmacy.utils.UtilsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRespository inventoryRespository;
    private final UtilsService utilsService;
    private final Environment env;
    private final ModelMapper modelMapper;

    @Autowired
    public InventoryServiceImpl(InventoryRespository inventoryRespository, UtilsService utilsService, Environment env, ModelMapper modelMapper) {
        this.inventoryRespository = inventoryRespository;
        this.utilsService = utilsService;
        this.env = env;
        this.modelMapper = modelMapper;
    }

    @Override
    public void synchronizeWeeklyConsumption(String authorization) {
        Iterable<InventoryEntity> inventoryEntityIterableIterable = inventoryRespository.findAll();
        List<String[]> identifierList = new ArrayList<>();
        inventoryEntityIterableIterable.forEach(inventoryEntity -> {
            InventoryEntity.PharmacyMedicationKey pharmacyMedicationKey = inventoryEntity.getPharmacyMedicationKey();
            identifierList.add(new String[]{pharmacyMedicationKey.getPharmacyId(), pharmacyMedicationKey.getMedicationId()});
        });

        ParameterizedTypeReference<List<String[]>> requestTypeReference = new ParameterizedTypeReference<>() {
        };
        List<String[]> returnValue = utilsService.webClientPost(env.getProperty("patients.url"), "/prescriptions/weekly-sum", identifierList, authorization, requestTypeReference, requestTypeReference);

        returnValue.forEach(entry -> {
            InventoryEntity inventoryEntity = inventoryRespository.findByPharmacyMedicationKey_PharmacyIdAndPharmacyMedicationKey_MedicationId(entry[0], entry[1])
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "pharmacy and medication", entry[0] + " " + entry[1]));
            if (entry[2] != null) {
                inventoryEntity.setVelocityOutWeekly(Integer.parseInt(entry[2]));
            } else {
                inventoryEntity.setVelocityOutWeekly(0);
            }
            inventoryRespository.save(inventoryEntity);
        });
    }

    @Override
    public InventoryDto findByPharmacyIdAndMedicationId(String pharmacyId, String medicationId) {
        InventoryEntity inventoryEntity = inventoryRespository
                .findByPharmacyMedicationKey_PharmacyIdAndPharmacyMedicationKey_MedicationId(pharmacyId, medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy or medication", "id", pharmacyId + " " + medicationId));
        InventoryDto inventoryDto = utilsService.mapEntityToDto(inventoryEntity);
        return inventoryDto;
    }

    @Override
    public List<InventoryDto> findByPharmacyId(String pharmacyId) {
        List<InventoryEntity> inventoryEntityList = inventoryRespository.findByPharmacyMedicationKey_PharmacyId(pharmacyId);
        return inventoryEntityList.stream()
                .map(utilsService::mapEntityToDto)
                .collect(Collectors.toList());
    }

}
