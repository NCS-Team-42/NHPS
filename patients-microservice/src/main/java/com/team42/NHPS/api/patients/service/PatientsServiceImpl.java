package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.data.PatientEntity;
import com.team42.NHPS.api.patients.data.PatientsRepository;
import com.team42.NHPS.api.patients.exception.ResourceNotFoundException;
import com.team42.NHPS.api.patients.shared.PatientDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientsServiceImpl implements PatientsService {

    private PatientsRepository patientsRepository;
    private ModelMapper modelMapper;

    @Autowired
    public PatientsServiceImpl(PatientsRepository patientsRepository, ModelMapper modelMapper) {
        this.patientsRepository = patientsRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<PatientDto> getAllPatients() {
        List<PatientDto> patientDtoList = new ArrayList<>();
        Iterable<PatientEntity> patientEntityList = patientsRepository.findAll();
        if (!patientEntityList.iterator().hasNext()) throw new ResourceNotFoundException("Patients", "all", null);
        patientEntityList.forEach(patientEntity -> patientDtoList.add(modelMapper.map(patientEntity, PatientDto.class)));
        return patientDtoList;
    }

    @Override
    public PatientDto getPatientByNric(String nric) {
        PatientEntity patientEntity = patientsRepository.findByNric(nric).orElseThrow(() -> new ResourceNotFoundException("Patient", "nric", nric));
        return modelMapper.map(patientEntity, PatientDto.class);
    }

    @Override
    public PatientDto createPatient(PatientDto patientDto) {
        PatientEntity patientEntity = patientsRepository.save(modelMapper.map(patientDto, PatientEntity.class));
        return modelMapper.map(patientEntity, PatientDto.class);
    }

    @Override
    public void deletePatient(String nric) {
        PatientDto patientDto = this.getPatientByNric(nric);
        patientsRepository.delete(modelMapper.map(patientDto, PatientEntity.class));
    }
}
