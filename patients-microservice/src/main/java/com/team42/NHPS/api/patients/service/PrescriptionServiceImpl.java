package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.data.PrescriptionEntity;
import com.team42.NHPS.api.patients.data.PrescriptionRepository;
import com.team42.NHPS.api.patients.exception.ResourceNotFoundException;
import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import com.team42.NHPS.api.patients.ui.models.MedicationResponseModel;
import com.team42.NHPS.api.patients.ui.models.PharmacyResponseModel;
import com.team42.NHPS.api.patients.ui.models.UpdateInventoryRequestModel;
import com.team42.NHPS.api.patients.utils.UtilService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    private PrescriptionRepository prescriptionRepository;
    private PatientsService patientsService;
    private UtilService utilService;
    private ModelMapper modelMapper;
    private Environment env;

    @Autowired
    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository, PatientsService patientsService, UtilService utilService, ModelMapper modelMapper, Environment env) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientsService = patientsService;
        this.utilService = utilService;
        this.modelMapper = modelMapper;
        this.env = env;
    }

    @Override
    public PrescriptionDto createPrescription(PrescriptionDto prescriptionDto, String authorization) {
        dtoValidityCheck(prescriptionDto, authorization); // throw exception when any of the ids not found
        PrescriptionEntity entity = modelMapper.map(prescriptionDto, PrescriptionEntity.class);
        PrescriptionEntity.PatientMedicationKey patientMedicationKey = new PrescriptionEntity.PatientMedicationKey(prescriptionDto.getPatientNric(), prescriptionDto.getMedicationId());
        entity.setPatientMedicationKey(patientMedicationKey);
        PrescriptionEntity prescriptionEntity = prescriptionRepository.save(entity);
        inventoryUpdate(new UpdateInventoryRequestModel("prescribe", List.of(prescriptionDto)), authorization);
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
    public String batchProcessConsumption(String nric, String authorization) { // trigger stored prod. Should be a cron job, depending on prescription date and last update date
        List<PrescriptionDto> prescriptionDtoList = getPrescriptionByNric(nric);
        String returnValue = inventoryUpdate(new UpdateInventoryRequestModel("dispense", prescriptionDtoList), authorization);
        prescriptionRepository.batchProcessConsumption(nric);
        return returnValue;
    }

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
