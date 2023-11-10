package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.data.PrescriptionEntity;
import com.team42.NHPS.api.patients.data.PrescriptionRepository;
import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import com.team42.NHPS.api.patients.utils.UtilService;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PrescriptionServiceImplTest {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UtilService utilService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Environment env;

    public static MockWebServer mockBackEnd;

    @MockBean
    private PrescriptionRepository prescriptionRepository;


    @Test
    public void createPrescriptionTest() {
        PrescriptionDto prescriptionDto = new PrescriptionDto("patientNric", "medicationId", "pharmacyId", 10, 0, 100);
        PrescriptionEntity entity = modelMapper.map(prescriptionDto, PrescriptionEntity.class);
        PrescriptionEntity.PatientMedicationKey patientMedicationKey = new PrescriptionEntity.PatientMedicationKey(prescriptionDto.getPatientNric(), prescriptionDto.getMedicationId());
        entity.setPatientMedicationKey(patientMedicationKey);
        entity.setPrescriptionDate(new Date(System.currentTimeMillis()));
        when(prescriptionRepository.save(entity)).thenReturn(entity);
        assertEquals(modelMapper.map(entity, PrescriptionDto.class), prescriptionDto);
    }

    @Test
    public void getPrescriptionByNric() {
        String nric = "S1234567B";

        PrescriptionEntity entity = new PrescriptionEntity(new PrescriptionEntity.PatientMedicationKey(nric, "medicationId"),
                "pharmacyId",
                10,
                0,
                100,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()));

        when(prescriptionRepository.findByPatientMedicationKey_PatientNric(nric)).thenReturn(List.of(entity));
        assertEquals(prescriptionService.getPrescriptionByNric(nric).get(0).getPatientNric(), nric);
    }

    @Test
    public void medicationCheck() throws InterruptedException, IOException {
//        mockBackEnd = new MockWebServer();
//        mockBackEnd.start();
//        String authorization = "authorization";
//        mockBackEnd.enqueue(
//                new MockResponse()
//                        .setHeader(HttpHeaders.AUTHORIZATION, authorization)
//        );
//
//        prescriptionService.medicationCheck("medicationId", authorization);
//        RecordedRequest request = mockBackEnd.takeRequest();
//
//        assertThat(request.getMethod()).isEqualTo("GET");
//        mockBackEnd.shutdown();
    }

    @Test
    public void pharmacyCheck() {
    }

    @Test
    public void dispenseMedication() {
    }

    @Test
    public void checkSum() {
    }

    @Test
    public void findAll() {
        String nric = "S1234567B";

        PrescriptionEntity entity = new PrescriptionEntity(new PrescriptionEntity.PatientMedicationKey(nric, "medicationId"),
                "pharmacyId",
                10,
                0,
                100,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()));
        when(prescriptionRepository.findAll()).thenReturn(List.of(entity));
        assertEquals(prescriptionService.findAll().size(), 1);
    }
}