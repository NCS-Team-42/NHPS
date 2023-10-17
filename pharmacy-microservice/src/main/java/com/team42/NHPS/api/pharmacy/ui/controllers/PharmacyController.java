package com.team42.NHPS.api.pharmacy.ui.controllers;

import com.team42.NHPS.api.pharmacy.service.InventoryService;
import com.team42.NHPS.api.pharmacy.service.PharmacyService;
import com.team42.NHPS.api.pharmacy.shared.InventoryDto;
import com.team42.NHPS.api.pharmacy.shared.PharmacyDto;
import com.team42.NHPS.api.pharmacy.shared.PharmacyUserMappingDto;
import com.team42.NHPS.api.pharmacy.ui.model.*;
import com.team42.NHPS.api.pharmacy.utils.UtilsService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pharmacies")
public class PharmacyController {

    @Value("${token.secret}")
    private String token;
    @Value("${server.port}")
    private String port;
    private final PharmacyService pharmacyService;
    private final InventoryService inventoryService;
    private final UtilsService utilsService;
    private final Environment environment;
    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PharmacyController(PharmacyService pharmacyService, InventoryService inventoryService, UtilsService utilsService, Environment environment, ModelMapper modelMapper) {
        this.pharmacyService = pharmacyService;
        this.inventoryService = inventoryService;
        this.utilsService = utilsService;
        this.environment = environment;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<PharmacyResponseModel>> getAllPharmacy() {
        List<PharmacyDto> pharmacyDtoList = pharmacyService.getAllPharmacy();
        List<PharmacyResponseModel> foundPharmacies = new ArrayList<>();
        pharmacyDtoList.forEach(pharmacyDto -> foundPharmacies.add(modelMapper.map(pharmacyDto, PharmacyResponseModel.class)));
        return ResponseEntity.status(HttpStatus.OK).body(foundPharmacies);
    }

    @GetMapping("/{pharmacyId}")
    public ResponseEntity<PharmacyResponseModel> getPharmacy(@PathVariable String pharmacyId) {
        PharmacyDto pharmacyDto = pharmacyService.getPharmacy(pharmacyId);
        PharmacyResponseModel foundPharmacy = modelMapper.map(pharmacyDto, PharmacyResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(foundPharmacy);
    }

    @PostMapping
    public ResponseEntity<PharmacyResponseModel> createPharmacy(@Valid @RequestBody CreatePharmacyRequestModel pharmacyRequestModel) {

        PharmacyDto createdPharmacyDto = pharmacyService.createPharmacy(modelMapper.map(pharmacyRequestModel, PharmacyDto.class));

        PharmacyResponseModel foundPharmacy = modelMapper.map(createdPharmacyDto, PharmacyResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(foundPharmacy);
    }

    @PostMapping("/{pharmacyId}/user/{userId}")
    public ResponseEntity<Map<String, String>> mapUserToPharmacy(@PathVariable String pharmacyId, @PathVariable String userId, @RequestHeader("Authorization") String authorization) {
        Map<String, String> map = pharmacyService.mapPharmacyToUser(new PharmacyUserMappingDto(pharmacyId, userId), authorization);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @PostMapping("/inventory/update")
    public ResponseEntity<String> updateInventory(@RequestBody UpdateInventoryRequestModel updateInventoryRequestModel) {
        log.info(updateInventoryRequestModel.toString());
        try {
            pharmacyService.updateInventory(updateInventoryRequestModel);
            return ResponseEntity.ok("Update inventory successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to update inventory");
        }
    }

    @GetMapping("/synchronize-weekly-consumption")
    public void synchronizeWeeklyConsumption(@RequestHeader("authorization") String authorization) {
        inventoryService.synchronizeWeeklyConsumption(authorization);
    }

    @GetMapping("/{pharmacyId}/medication/{medicationId}")
    public ResponseEntity<InventorySpecificMedicationResponseModel> findInventoryByPharmacyIdAndMedicationId(@PathVariable String pharmacyId, @PathVariable String medicationId, @RequestHeader("authorization") String authorization) {
        InventoryDto inventoryDto = inventoryService.findByPharmacyIdAndMedicationId(pharmacyId, medicationId);
        PharmacyResponseModel pharmacyResponseModel = modelMapper.map(pharmacyService.getPharmacy(pharmacyId), PharmacyResponseModel.class);

        ParameterizedTypeReference<MedicationResponseModel> responseTypeReference = new ParameterizedTypeReference<>() {};
        MedicationResponseModel medicationResponseModel = utilsService.webClientGet(environment.getProperty("medication.url"), String.format("/%s", medicationId), authorization, responseTypeReference);

        InventorySpecificMedicationResponseModel inventoryResponseModel = new InventorySpecificMedicationResponseModel();
        inventoryResponseModel.setQuantity(inventoryDto.getQuantity());
        inventoryResponseModel.setVelocityOutWeekly(inventoryDto.getVelocityOutWeekly());
        inventoryResponseModel.setVelocityInWeekly(inventoryDto.getVelocityInWeekly());
        inventoryResponseModel.setPharmacyResponseModel(pharmacyResponseModel);
        inventoryResponseModel.setMedicationResponseModel(medicationResponseModel);
        return ResponseEntity.ok(inventoryResponseModel);
    }

    @GetMapping("/{pharmacyId}/inventory")
    public ResponseEntity<InventoryPharmacyResponseModel> findInventoryByPharmacyId(@PathVariable String pharmacyId, @RequestHeader("authorization") String authorization) {
        InventoryPharmacyResponseModel inventoryPharmacyResponseModel = new InventoryPharmacyResponseModel();
        PharmacyResponseModel pharmacyResponseModel = modelMapper.map(pharmacyService.getPharmacy(pharmacyId), PharmacyResponseModel.class);
        List<InventoryPharmacyResponseModel.MedicationInfo> medicationInfoList = new ArrayList<>();

        List<InventoryDto> inventoryDtoList = inventoryService.findByPharmacyId(pharmacyId);
        inventoryDtoList.forEach(inventoryDto -> {
            InventoryPharmacyResponseModel.MedicationInfo medicationInfo = new InventoryPharmacyResponseModel.MedicationInfo();
            ParameterizedTypeReference<MedicationResponseModel> responseTypeReference = new ParameterizedTypeReference<>() {};
            MedicationResponseModel medicationResponseModel = utilsService.webClientGet(environment.getProperty("medication.url"), String.format("/%s", inventoryDto.getMedicineId()), authorization, responseTypeReference);

            medicationInfo.setMedicationResponseModel(medicationResponseModel);
            medicationInfo.setQuantity(inventoryDto.getQuantity());
            medicationInfo.setVelocityInWeekly(inventoryDto.getVelocityInWeekly());
            medicationInfo.setVelocityOutWeekly(inventoryDto.getVelocityOutWeekly());

            medicationInfoList.add(medicationInfo);
        });

        inventoryPharmacyResponseModel.setPharmacyResponseModel(pharmacyResponseModel);
        inventoryPharmacyResponseModel.setMedicationInfoList(medicationInfoList);

        return ResponseEntity.ok(inventoryPharmacyResponseModel);
    }

    @GetMapping("/status/check")
    public String status(@RequestHeader("Authorization") String authorizationHeader) {
        String returnValue = "Working on port " + port + " with token " + token + ".\nToken from environment "
                + environment.getProperty("token.secret") + "\nAuthorizationHeader = " + authorizationHeader
                + ".\nMy application environment = " + environment.getProperty("myapplication.environment");
        log.info(returnValue);
        return returnValue;
    }

    @GetMapping("/ip")
    public String getIp() {
        String returnValue;

        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            returnValue = ipAddr.getHostAddress();
        } catch (UnknownHostException ex) {
            returnValue = ex.getLocalizedMessage();
        }

        return returnValue;
    }

}
