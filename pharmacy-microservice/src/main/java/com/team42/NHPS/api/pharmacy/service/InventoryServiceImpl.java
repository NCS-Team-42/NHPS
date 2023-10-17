package com.team42.NHPS.api.pharmacy.service;

import com.team42.NHPS.api.pharmacy.data.InventoryEntity;
import com.team42.NHPS.api.pharmacy.data.InventoryRespository;
import com.team42.NHPS.api.pharmacy.exception.ResourceNotFoundException;
import com.team42.NHPS.api.pharmacy.shared.PrescriptionDto;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    private InventoryRespository inventoryRespository;
    private Environment env;
    private ModelMapper modelMapper;

    @Autowired
    public InventoryServiceImpl(InventoryRespository inventoryRespository, Environment env, ModelMapper modelMapper) {
        this.inventoryRespository = inventoryRespository;
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
        List<String[]> returnValue = prescriptionsWeeklySum(identifierList, authorization);
        returnValue.forEach(entry -> {
            InventoryEntity inventoryEntity = inventoryRespository.findByPharmacyMedicationKey_PharmacyIdAndPharmacyMedicationKey_MedicationId(entry[0], entry[1])
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "pharmacy and medication", entry[0] + " " + entry[1]));
            if (!"null".equals(entry[2])) {
                inventoryEntity.setVelocityOutWeekly(Integer.parseInt(entry[2]));
            }
            inventoryRespository.save(inventoryEntity);
        });
    }

    private List<String[]> prescriptionsWeeklySum(List<String[]> identifierList, String authorization) {
        return WebClient.create(env.getProperty("patients.url"))
                .method(HttpMethod.POST).uri("/prescriptions/weekly-sum")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .body(Mono.just(identifierList), new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String[]>>() {
                })
                .block();
    }
}
