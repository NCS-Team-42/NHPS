package com.team42.NHPS.api.patients.ui.controllers;

import com.team42.NHPS.api.patients.service.PatientsService;
import com.team42.NHPS.api.patients.service.PrescriptionService;
import com.team42.NHPS.api.patients.shared.DispenseDto;
import com.team42.NHPS.api.patients.shared.PatientDto;
import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import com.team42.NHPS.api.patients.ui.models.PrescriptionResponseModel;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
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
import java.util.stream.Collectors;


@RestController
@RequestMapping("/patients")
public class PatientsController {

    @Value("${token.secret}")
    private String token;
    @Value("${server.port}")
    private String port;
    private final PatientsService patientsService;
    private final PrescriptionService prescriptionService;
    private final Environment environment;
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
        return new ResponseEntity<>(patientsService.createPatient(patientDto), HttpStatus.CREATED);
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<PatientDto>> createPatients(@Valid @RequestBody List<PatientDto> patientDtoList) {
        List<PatientDto> patientDtos = new ArrayList<>();
        patientDtoList.forEach(patientDto -> patientDtos.add(patientsService.createPatient(patientDto)));
        return new ResponseEntity<>(patientDtos, HttpStatus.CREATED);
    }

    @PostMapping("/prescription")
    public ResponseEntity<PrescriptionDto> createPrescription(@Valid @RequestBody PrescriptionDto prescriptionDto, @RequestHeader("Authorization") String authorization) {
        return new ResponseEntity<>(prescriptionService.createPrescription(prescriptionDto, authorization), HttpStatus.CREATED);
    }

    @PostMapping("/prescriptions")
    @Transactional
    public ResponseEntity<List<PrescriptionDto>> createPrescriptions(@Valid @RequestBody List<PrescriptionDto> prescriptionDtoList, @RequestHeader("Authorization") String authorization) {
        prescriptionDtoList.stream().map(prescriptionDto -> prescriptionService.createPrescription(prescriptionDto, authorization)).collect(Collectors.toList());
        return new ResponseEntity<>(prescriptionDtoList, HttpStatus.CREATED);
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
                    prescriptionResponseModel.setPrescribedDosage(dto.getPrescribedDosage());
                    prescriptionResponseModel.setPharmacyResponseModel(prescriptionService.pharmacyCheck(dto.getPharmacyId(), authorization));
                    prescriptionResponseModel.setMedicationResponseModel(prescriptionService.medicationCheck(dto.getMedicationId(), authorization));
                    return prescriptionResponseModel;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(prescriptionResponseModelList);
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<List<PrescriptionDto>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.findAll());
    }

//    @GetMapping("/process-prescription/manual/{nric}")
//    public ResponseEntity<String> processPrescriptionBatchJobManual(@PathVariable String nric, @RequestHeader("Authorization") String authorization) {
//        String returnValue = prescriptionService.batchProcessConsumption(nric, authorization);
//        return ResponseEntity.ok(returnValue);
//    }

    @PostMapping("/dispense")
    public ResponseEntity<PrescriptionDto> dispenseMedication(@Valid @RequestBody DispenseDto dispenseDto, @RequestHeader("Authorization") String authorization) {
        return new ResponseEntity<>(prescriptionService.dispenseMedication(dispenseDto, authorization), HttpStatus.OK);
    }

    @PostMapping("/dispenses")
    @Transactional
    public ResponseEntity<List<PrescriptionDto>> dispensesMedication(@Valid @RequestBody List<DispenseDto> dispenseDtoList, @RequestHeader("Authorization") String authorization) {
        List<PrescriptionDto> prescriptionDtoList = new ArrayList<>();
        dispenseDtoList.forEach(dispenseDto -> prescriptionDtoList.add(prescriptionService.dispenseMedication(dispenseDto, authorization)));
        return new ResponseEntity<>(prescriptionDtoList, HttpStatus.OK);
    }

    @PostMapping("/prescriptions/weekly-sum")
    public List<String[]> prescriptionsWeeklySum(@RequestBody List<String[]> identifierList) {
        List<Pair<String, String>> pairList = new ArrayList<>();
        identifierList.forEach(strings -> pairList.add(Pair.of(strings[0], strings[1])));
        List<Triple<String, String, Integer>> tripleList = prescriptionService.checkSum(pairList);
        List<String[]> strings = new ArrayList<>();
        tripleList.forEach(triple -> strings.add(new String[]{
                triple.getLeft(), triple.getMiddle(), triple.getRight() != null ? String.valueOf(triple.getRight()) : null
        }));
        return strings;
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
