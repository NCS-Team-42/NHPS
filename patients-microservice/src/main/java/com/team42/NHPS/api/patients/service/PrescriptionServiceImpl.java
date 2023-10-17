package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.data.PrescriptionEntity;
import com.team42.NHPS.api.patients.data.PrescriptionRepository;
import com.team42.NHPS.api.patients.exception.ResourceNotFoundException;
import com.team42.NHPS.api.patients.shared.DispenseDto;
import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import com.team42.NHPS.api.patients.ui.models.MedicationResponseModel;
import com.team42.NHPS.api.patients.ui.models.PharmacyResponseModel;
import com.team42.NHPS.api.patients.ui.models.UpdateInventoryRequestModel;
import com.team42.NHPS.api.patients.utils.UtilService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientsService patientsService;
    private final UtilService utilService;
    private final ModelMapper modelMapper;
    private final Environment env;

    @Autowired
    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository, PatientsService patientsService, UtilService utilService, ModelMapper modelMapper, Environment env) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientsService = patientsService;
        this.utilService = utilService;
        this.modelMapper = modelMapper;
        this.env = env;
    }

    @Override
    @Transactional
    public PrescriptionDto createPrescription(PrescriptionDto prescriptionDto, String authorization) {
        dtoValidityCheck(prescriptionDto, authorization); // throw exception when any of the ids not found
        PrescriptionEntity entity = modelMapper.map(prescriptionDto, PrescriptionEntity.class);
        PrescriptionEntity.PatientMedicationKey patientMedicationKey = new PrescriptionEntity.PatientMedicationKey(prescriptionDto.getPatientNric(), prescriptionDto.getMedicationId());
        entity.setPatientMedicationKey(patientMedicationKey);
        entity.setPrescriptionDate(new Date(System.currentTimeMillis()));

        Optional<PrescriptionEntity> prescriptionEntityOptional = prescriptionRepository
                .findByPatientMedicationKey_PatientNricAndPatientMedicationKey_MedicationId(prescriptionDto.getPatientNric(), prescriptionDto.getMedicationId());

        // getting delta to send to pharmacy microservice
        prescriptionEntityOptional.ifPresent(prescription -> prescriptionDto.setConsumptionWeekly(prescriptionDto.getConsumptionWeekly() - prescription.getConsumptionWeekly()));

        PrescriptionEntity prescriptionEntity = prescriptionRepository.save(entity);

        inventoryUpdate(new UpdateInventoryRequestModel("prescribe", prescriptionDto, 0), authorization);

        return modelMapper.map(prescriptionEntity, PrescriptionDto.class);
    }

    @Override
    public List<PrescriptionDto> getPrescriptionByNric(String nric) {
        List<PrescriptionEntity> prescriptionEntityList = prescriptionRepository.findByPatientMedicationKey_PatientNric(nric);
        return utilService.entityToDtoMapList(prescriptionEntityList, PrescriptionDto.class);
    }

    @Override
    public MedicationResponseModel medicationCheck(String medicationId, String authorization) {
        WebClient client = WebClient.create(env.getProperty("medication.url"));
        UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        RequestBodySpec bodySpec = uriSpec.uri("/" + medicationId);
        ResponseSpec responseSpec = bodySpec.header(HttpHeaders.AUTHORIZATION, authorization).retrieve();
        return responseSpec.bodyToMono(MedicationResponseModel.class).block();
    }

    @Override
    public PharmacyResponseModel pharmacyCheck(String pharmacyId, String authorization) {
        WebClient client = WebClient.create(env.getProperty("pharmacy.url"));
        UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        RequestBodySpec bodySpec = uriSpec.uri("/" + pharmacyId);
        ResponseSpec responseSpec = bodySpec.header(HttpHeaders.AUTHORIZATION, authorization).retrieve();
        return responseSpec.bodyToMono(PharmacyResponseModel.class).block();
    }

    @Override
    @Transactional
    public PrescriptionDto dispenseMedication(DispenseDto dispenseDto, String authorization) {

        PrescriptionEntity prescriptionEntity = prescriptionRepository
                .findByPatientMedicationKey_PatientNricAndPatientMedicationKey_MedicationId(dispenseDto.getPatientNric(), dispenseDto.getMedicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "patient nric, medication id", dispenseDto.getPatientNric() + " " + dispenseDto.getMedicationId()));

        if (prescriptionEntity.getPrescribedDosage() > dispenseDto.getQuantity()) {
            prescriptionEntity.setDoseLeft(prescriptionEntity.getDoseLeft() + dispenseDto.getQuantity());
            prescriptionEntity.setPrescribedDosage(prescriptionEntity.getPrescribedDosage() - dispenseDto.getQuantity());
            prescriptionEntity.setDispenseDate(new Date(System.currentTimeMillis()));
        } else {
            prescriptionEntity.setDoseLeft(prescriptionEntity.getDoseLeft() + prescriptionEntity.getPrescribedDosage());
            dispenseDto.setQuantity(prescriptionEntity.getPrescribedDosage());
            prescriptionEntity.setPrescribedDosage(0);
        }

        prescriptionEntity = prescriptionRepository.save(prescriptionEntity);

        PrescriptionDto prescriptionDto = modelMapper.map(prescriptionEntity, PrescriptionDto.class);
        inventoryUpdate(new UpdateInventoryRequestModel("dispense", prescriptionDto, dispenseDto.getQuantity()), authorization);
        return prescriptionDto;
    }

    @Override
    public List<Triple<String, String, Integer>> checkSum(List<Pair<String, String>> pairList) {
        List<Triple<String, String, Integer>> tripleList = new ArrayList<>();
        pairList.forEach(pair -> tripleList.add(Triple.of(pair.getLeft(), pair.getRight(), prescriptionRepository.getSumByPharmacyIdAndMedicationId(pair.getLeft(), pair.getRight()))));
        return tripleList;
    }

    @Override
    public List<PrescriptionDto> findAll() {
        List<PrescriptionDto> prescriptionDtoList = new ArrayList<>();
        Iterable<PrescriptionEntity> prescriptionEntityIterable = prescriptionRepository.findAll();
        prescriptionEntityIterable.forEach(prescriptionEntity -> prescriptionDtoList.add(modelMapper.map(prescriptionEntity, PrescriptionDto.class)));
        return prescriptionDtoList;
    }

//    @Override
//    @Transactional
//    public String batchProcessConsumption(String nric, String authorization) { // trigger stored prod. Should be a cron job, depending on prescription date and last update date
//        List<PrescriptionDto> prescriptionDtoList = getPrescriptionByNric(nric);
//        String returnValue = inventoryUpdate(new UpdateInventoryRequestModel("dispense", prescriptionDtoList), authorization);
//        prescriptionRepository.batchProcessConsumption(nric);
//        return returnValue;
//    }

    private void dtoValidityCheck(PrescriptionDto prescriptionDto, String authorization) {
        MedicationResponseModel medicationResponseModel = medicationCheck(prescriptionDto.getMedicationId(), authorization);
        if (medicationResponseModel == null)
            throw new ResourceNotFoundException("Medication", "id", prescriptionDto.getMedicationId());
        PharmacyResponseModel pharmacyResponseModel = pharmacyCheck(prescriptionDto.getPharmacyId(), authorization);
        if (pharmacyResponseModel == null)
            throw new ResourceNotFoundException("Pharmacy", "id", prescriptionDto.getPharmacyId());
        patientsService.getPatientByNric(prescriptionDto.getPatientNric());
    }

    private String inventoryUpdate(UpdateInventoryRequestModel updateInventoryRequestModel, String authorization) {
        return WebClient.create(env.getProperty("pharmacy.url"))
                .method(HttpMethod.POST)
                .uri("/inventory/update")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .body(Mono.just(updateInventoryRequestModel), UpdateInventoryRequestModel.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
