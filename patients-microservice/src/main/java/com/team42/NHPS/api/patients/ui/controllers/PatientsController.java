package com.team42.NHPS.api.patients.ui.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.team42.NHPS.api.patients.service.PrescriptionService;
import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import com.team42.NHPS.api.patients.ui.models.PharmacyResponseModel;
import com.team42.NHPS.api.patients.ui.models.PrescriptionResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import com.team42.NHPS.api.patients.service.PatientsService;
import com.team42.NHPS.api.patients.shared.PatientDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/patients")
public class PatientsController {

    @Value("${token.secret}")
    private String token;
    @Value("${server.port}")
    private String port;
    private PatientsService patientsService;
    private PrescriptionService prescriptionService;
    private Environment environment;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PatientsController(PatientsService patientsService, PrescriptionService prescriptionService, Environment env) {
        this.patientsService = patientsService;
        this.prescriptionService = prescriptionService;
        this.environment = env;
    }

    @GetMapping
    public ResponseEntity<List<PatientDto>> getPatients() {
        return ResponseEntity.ok(patientsService.getAllPatients());
    }

    @GetMapping("/{nric}")
    public ResponseEntity<PatientDto> getPatientByNric(@PathVariable String nric) {
        return ResponseEntity.ok(patientsService.getPatientByNric(nric));
    }

    @DeleteMapping("/{nric}")
    public ResponseEntity<String> deletePatient(@PathVariable String nric) {
        patientsService.deletePatient(nric);
        return ResponseEntity.ok("Patient of nric: " + nric + " deleted");
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        return new ResponseEntity(patientsService.createPatient(patientDto), HttpStatus.CREATED);
    }

    @PostMapping("/prescription")
    public ResponseEntity<PrescriptionDto> createPrescription(@Valid @RequestBody PrescriptionDto prescriptionDto, @RequestHeader("Authorization") String authorization) {
        return new ResponseEntity<>(prescriptionService.createPrescription(prescriptionDto, authorization), HttpStatus.CREATED);
    }

    @GetMapping("/{nric}/prescription")
    public ResponseEntity<List<PrescriptionResponseModel>> getPrescriptionByNric(@PathVariable String nric, @RequestHeader("Authorization") String authorization) {
        List<PrescriptionDto> prescriptionDtoList = prescriptionService.getPrescriptionByNric(nric);
        List<PrescriptionResponseModel> prescriptionResponseModelList =
                prescriptionDtoList.stream().map(dto -> {
                    PrescriptionResponseModel prescriptionResponseModel = new PrescriptionResponseModel();
                    prescriptionResponseModel.setNric(dto.getPatientNric());
                    prescriptionResponseModel.setConsumptionWeekly(dto.getConsumptionWeekly());
                    prescriptionResponseModel.setDoseLeft(dto.getDoseLeft());
                    prescriptionResponseModel.setPharmacyResponseModel(prescriptionService.pharmacyCheck(dto.getPharmacyId(), authorization));
                    prescriptionResponseModel.setMedicationResponseModel(prescriptionService.medicationCheck(dto.getMedicationId(), authorization));
                    return prescriptionResponseModel;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(prescriptionResponseModelList);
    }

    @GetMapping("/process-prescription/manual/{nric}")
    public ResponseEntity<String> processPrescriptionBatchJobManual(@PathVariable String nric, @RequestHeader("Authorization") String authorization) {
        String returnValue = prescriptionService.batchProcessConsumption(nric, authorization);
        return ResponseEntity.ok(returnValue);
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
