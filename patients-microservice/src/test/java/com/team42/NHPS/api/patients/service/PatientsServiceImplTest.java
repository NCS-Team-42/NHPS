package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.data.PatientEntity;
import com.team42.NHPS.api.patients.data.PatientsRepository;
import com.team42.NHPS.api.patients.exception.ResourceNotFoundException;
import com.team42.NHPS.api.patients.shared.PatientDto;
import com.team42.NHPS.api.patients.utils.UtilService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientsServiceImplTest {

    @Autowired
    PatientsService patientsService;
    @MockBean
    PatientsRepository patientsRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UtilService utilService;

    @Test
    public void getAllPatients() {
        PatientEntity patientEntity = new PatientEntity("S1234567B", "Mock", "Name", "mock@gmail.com", new Date(System.currentTimeMillis()), "91234567", "123456");
        when(patientsRepository.findAll()).thenReturn(List.of(patientEntity));
        List<PatientDto> patientEntityList = patientsService.getAllPatients();
        assertEquals(patientEntityList.size(), 1);
    }

    @Test
    public void getPatientByNric() {
        String nric = "S1234567B";
        PatientEntity patientEntity = new PatientEntity("S1234567B", "Mock", "Name", "mock@gmail.com", new Date(System.currentTimeMillis()), "91234567", "123456");

        when(patientsRepository.findByNric(nric)).thenReturn(Optional.of(patientEntity));
        PatientEntity entity = patientsRepository.findByNric(nric).orElseThrow(() -> new ResourceNotFoundException("Patient", "nric", nric));
        assertEquals(entity.getNric(), nric);
    }

    @Test
    public void createPatient() {
        PatientEntity patientEntity = new PatientEntity("S1234567B", "Mock", "Name", "mock@gmail.com", new Date(System.currentTimeMillis()), "91234567", "123456");
        when(patientsRepository.save(patientEntity)).thenReturn(patientEntity);
        PatientDto mocked = patientsService.createPatient(modelMapper.map(patientEntity, PatientDto.class));
        assertEquals(mocked.getNric(), patientEntity.getNric());
    }

    @Test
    public void editPatient() {
        PatientEntity patientEntity = new PatientEntity("S1234567B", "Mock", "Name", "mock@gmail.com", new Date(System.currentTimeMillis()), "91234567", "123456");
        when(patientsRepository.save(patientEntity)).thenReturn(patientEntity);
        PatientDto mocked = patientsService.createPatient(modelMapper.map(patientEntity, PatientDto.class));
        assertEquals(mocked.getNric(), patientEntity.getNric());
    }

    @Test
    public void deletePatient() {

    }
}