package com.team42.NHPS.api.pharmacy.service;

import com.team42.NHPS.api.pharmacy.data.*;
import com.team42.NHPS.api.pharmacy.exception.ResourceNotFoundException;
import com.team42.NHPS.api.pharmacy.shared.PharmacyDto;
import com.team42.NHPS.api.pharmacy.shared.PharmacyUserMappingDto;
import com.team42.NHPS.api.pharmacy.shared.PrescriptionDto;
import com.team42.NHPS.api.pharmacy.ui.model.UpdateInventoryRequestModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import java.util.*;

@Service
public class PharmacyServiceImpl implements PharmacyService {

    private PharmacyRepository pharmacyRepository;
    private PharmacyUserMappingRepository pharmacyUserMappingRepository;
    private InventoryRespository inventoryRespository;
    private ModelMapper modelMapper;
    private Environment env;

    @Autowired
    public PharmacyServiceImpl(PharmacyRepository pharmacyRepository, PharmacyUserMappingRepository pharmacyUserMappingRepository,
                               ModelMapper modelMapper, Environment env, InventoryRespository inventoryRespository) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyUserMappingRepository = pharmacyUserMappingRepository;
        this.inventoryRespository = inventoryRespository;
        this.modelMapper = modelMapper;
        this.env = env;
    }

    @Override
    public PharmacyDto getPharmacy(String pharmacyId) {
        PharmacyEntity foundPharmacy = pharmacyRepository.findByPharmacyId(pharmacyId).orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", pharmacyId));
        return modelMapper.map(foundPharmacy, PharmacyDto.class);
    }

    @Override
    public PharmacyDto createPharmacy(PharmacyDto pharmacyDto) {
        PharmacyEntity pharmacyEntity = modelMapper.map(pharmacyDto, PharmacyEntity.class);
        pharmacyEntity.setPharmacyId(UUID.randomUUID().toString());
        PharmacyEntity foundPharmacy = pharmacyRepository.save(pharmacyEntity);
        return modelMapper.map(foundPharmacy, PharmacyDto.class);
    }

    @Override
    public List<PharmacyDto> getAllPharmacy() {
        List<PharmacyDto> pharmacyDtoList = new ArrayList<>();
        Iterable<PharmacyEntity> pharmacyEntityIterator = pharmacyRepository.findAll();
        if (!pharmacyEntityIterator.iterator().hasNext())
            throw new ResourceNotFoundException("Pharmacies", "all", null);
        pharmacyEntityIterator.forEach(pharmacyEntity -> pharmacyDtoList.add(modelMapper.map(pharmacyEntity, PharmacyDto.class)));
        return pharmacyDtoList;
    }

    @Override
    public Map<String, String> mapPharmacyToUser(PharmacyUserMappingDto pharmacyUserMappingDto, String authorization) {
        WebClient client = WebClient.create(env.getProperty("users.url"));
        UriSpec<RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        RequestBodySpec bodySpec = uriSpec.uri("/" + pharmacyUserMappingDto.getUserId());
        ResponseSpec responseSpec = bodySpec.header(HttpHeaders.AUTHORIZATION, authorization).retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();

        PharmacyDto foundPharmacy = this.getPharmacy(pharmacyUserMappingDto.getPharmacyId()); // to check if pharmacy id exists, if not throw exception
        Map<String, String> map = new HashMap<>();
        map.put("User", responseBody);
        map.put("Pharmacy", foundPharmacy.getPharmacyName());

        PharmacyUserMappingEntity.PharmacyUserMappingId pharmacyUserMappingId = new PharmacyUserMappingEntity.PharmacyUserMappingId(pharmacyUserMappingDto.getPharmacyId(), pharmacyUserMappingDto.getUserId());
        PharmacyUserMappingEntity pharmacyUserMappingEntity = new PharmacyUserMappingEntity(pharmacyUserMappingId);
        pharmacyUserMappingRepository.save(pharmacyUserMappingEntity);

        return map;
    }

    @Override
    public void updateInventory(UpdateInventoryRequestModel updateInventoryRequestModel) { // on prescribe and dispense
        updateInventoryRequestModel.getPrescriptionDtoList().forEach(prescriptionDto -> {
            InventoryEntity inventoryEntity = inventoryRespository.findByPharmacyMedicationKey_PharmacyIdAndPharmacyMedicationKey_MedicationId(prescriptionDto.getPharmacyId(),
                    prescriptionDto.getMedicationId()).orElseThrow(() -> new ResourceNotFoundException("Inventory", "pharmacy and medication", prescriptionDto.getPharmacyId() + " " +
                    prescriptionDto.getMedicationId()));
            if ("dispense".equals(updateInventoryRequestModel.getAction())) {
                inventoryEntity.setQuantity(inventoryEntity.getQuantity() - prescriptionDto.getConsumptionWeekly());
            } else if ("prescribe".equals(updateInventoryRequestModel.getAction())) {
                inventoryEntity.setVelocityOutWeekly(inventoryEntity.getVelocityOutWeekly() + prescriptionDto.getConsumptionWeekly());
            }
            inventoryRespository.save(inventoryEntity);
        });
    }
}
