package com.team42.NHPS.api.medication.ui.controller;

import com.team42.NHPS.api.medication.service.MedicationService;
import com.team42.NHPS.api.medication.shared.MedicationDto;
import com.team42.NHPS.api.medication.ui.model.CreateMedicationRequestModel;
import com.team42.NHPS.api.medication.ui.model.MedicationResponseModel;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/medication")
public class MedicationController {
    @Value("${token.secret}")
    private String token;
    @Value("${server.port}")
    private String port;
    private MedicationService medicationService;
    private Environment environment;
    private ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MedicationController(MedicationService medicationService, Environment environment, ModelMapper modelMapper) {
        this.medicationService = medicationService;
        this.environment = environment;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{medicationID}")
    public ResponseEntity<MedicationResponseModel> getMedication(@PathVariable String medicationID) {
        MedicationDto medicationDto = medicationService.findByMedicationId(medicationID);
        MedicationResponseModel medicationResponseModel = modelMapper.map(medicationDto, MedicationResponseModel.class);
        return ResponseEntity.ok(medicationResponseModel);
    }

    @GetMapping
    public ResponseEntity<List<MedicationResponseModel>> getAllMedication() {
        List<MedicationDto> medicationDtoList = medicationService.findAll();
        List<MedicationResponseModel> medicationResponseModelList = new ArrayList<>();
        medicationDtoList.forEach(medicationDto -> medicationResponseModelList.add(modelMapper.map(medicationDto, MedicationResponseModel.class)));
        return ResponseEntity.ok(medicationResponseModelList);
    }

    @PostMapping
    public ResponseEntity<MedicationResponseModel> createMedication(@Valid @RequestBody CreateMedicationRequestModel createMedicationRequestModel) {
        MedicationDto medicationDto = medicationService.createMedication(modelMapper.map(createMedicationRequestModel, MedicationDto.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(medicationDto, MedicationResponseModel.class));
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
