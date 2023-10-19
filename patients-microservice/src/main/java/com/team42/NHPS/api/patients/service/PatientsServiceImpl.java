package com.team42.NHPS.api.patients.service;

import com.team42.NHPS.api.patients.data.PatientEntity;
import com.team42.NHPS.api.patients.data.PatientsRepository;
import com.team42.NHPS.api.patients.exception.ResourceNotFoundException;
import com.team42.NHPS.api.patients.shared.PatientDto;
import com.team42.NHPS.api.patients.utils.UtilService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientsServiceImpl implements PatientsService {

    private PatientsRepository patientsRepository;
    private UtilService utilService;
    private ModelMapper modelMapper;

    @Autowired
    public PatientsServiceImpl(PatientsRepository patientsRepository, ModelMapper modelMapper, UtilService utilService) {
        this.patientsRepository = patientsRepository;
        this.utilService = utilService;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<PatientDto> getAllPatients() {
        Iterable<PatientEntity> patientEntityIterable = patientsRepository.findAll();
        List<PatientEntity> patientEntityList = new ArrayList<>();
        if (!patientEntityIterable.iterator().hasNext()) throw new ResourceNotFoundException("Patients", "all", null);
        patientEntityIterable.forEach(patientEntityList::add);
        return utilService.entityToDtoMapList(patientEntityList, PatientDto.class);
    }

    @Override
    public PatientDto getPatientByNric(String nric) {
        PatientEntity patientEntity = patientsRepository.findByNric(nric).orElseThrow(() -> new ResourceNotFoundException("Patient", "nric", nric));
        return modelMapper.map(patientEntity, PatientDto.class);
    }

    @Override
    public PatientDto createPatient(PatientDto patientDto) {
        if (patientsRepository.findByNricOrEmail(patientDto.getNric(), patientDto.getEmail()) != null) {
            throw new DataIntegrityViolationException("Patient already exist");
        }
        PatientEntity patientEntity = patientsRepository.save(modelMapper.map(patientDto, PatientEntity.class));
        return modelMapper.map(patientEntity, PatientDto.class);
    }

    @Override
    public void deletePatient(String nric) {
        PatientDto patientDto = this.getPatientByNric(nric);
        patientsRepository.delete(modelMapper.map(patientDto, PatientEntity.class));
    }
}
