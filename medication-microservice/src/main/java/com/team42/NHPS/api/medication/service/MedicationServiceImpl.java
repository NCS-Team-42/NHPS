package com.team42.NHPS.api.medication.service;

import com.team42.NHPS.api.medication.data.MedicationEntity;
import com.team42.NHPS.api.medication.data.MedicationRepository;
import com.team42.NHPS.api.medication.exception.ResourceNotFoundException;
import com.team42.NHPS.api.medication.shared.MedicationDto;
import com.team42.NHPS.api.medication.ui.model.CreateMedicationRequestModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MedicationServiceImpl implements MedicationService {
    private MedicationRepository medicationRepository;
    private ModelMapper modelMapper;

    @Autowired
    public MedicationServiceImpl(MedicationRepository medicationRepository, ModelMapper modelMapper) {
        this.medicationRepository = medicationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public MedicationDto findByMedicationId(String medicationID) {
        MedicationEntity medicationEntity = medicationRepository.findByMedicationId(medicationID).orElseThrow(() -> new ResourceNotFoundException("Medication", "id", medicationID));
        return modelMapper.map(medicationEntity, MedicationDto.class);
    }

    @Override
    public List<MedicationDto> findAll() {
        Iterable<MedicationEntity> medicationEntityIterable = medicationRepository.findAll();
        if (!medicationEntityIterable.iterator().hasNext())
            throw new ResourceNotFoundException("Medication", "all", null);
        List<MedicationDto> medicationDtoList = new ArrayList<>();
        medicationEntityIterable.forEach(medicationEntity -> medicationDtoList.add(modelMapper.map(medicationEntity, MedicationDto.class)));
        return medicationDtoList;
    }

    @Override
    public MedicationDto createMedication(MedicationDto medicationDto) {
        MedicationEntity mappedEntity = modelMapper.map(medicationDto, MedicationEntity.class);
        mappedEntity.setMedicationId(UUID.randomUUID().toString());
        MedicationEntity medicationEntity = medicationRepository.save(mappedEntity);
        return modelMapper.map(medicationEntity, MedicationDto.class);
    }
}
