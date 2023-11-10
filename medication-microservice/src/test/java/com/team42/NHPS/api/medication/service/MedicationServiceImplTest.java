package com.team42.NHPS.api.medication.service;

import com.team42.NHPS.api.medication.data.MedicationEntity;
import com.team42.NHPS.api.medication.data.MedicationRepository;
import com.team42.NHPS.api.medication.exception.ResourceNotFoundException;
import com.team42.NHPS.api.medication.shared.MedicationDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MedicationServiceImplTest {
    @Autowired
    MedicationService service;
    @MockBean
    MedicationRepository repository;
    @Autowired
    ModelMapper modelMapper;

    @Test
    public void findByMedicationId() {
        String medicationId = "medicationId";
        when(repository.findByMedicationId(medicationId)).thenReturn(Optional.of(new MedicationEntity(1, medicationId, "test", "test description", "test activeIngredient", "test dosage", "test instructions")));
        MedicationEntity medicationEntity = repository.findByMedicationId(medicationId).orElseThrow(() -> new ResourceNotFoundException("Medication", "id", medicationId));
        assertEquals(medicationEntity.getMedicationId(), medicationId);
    }

    @Test
    public void findAll() {
        String medicationId = "medicationId";
        MedicationEntity medicationEntity = new MedicationEntity(1, medicationId, "test", "test description", "test activeIngredient", "test dosage", "test instructions");
        when(repository.findAll()).thenReturn(List.of(medicationEntity));
        assertEquals(service.findAll().size(), 1);
    }

    @Test
    public void createMedication() {
        MedicationDto dto = new MedicationDto();
        dto.setName("test");
        dto.setDescription("test description");
        dto.setActiveIngredient("test activeIngredient");
        dto.setDosage("test dosage");
        dto.setInstructions("test instructions");

        MedicationEntity entity = modelMapper.map(dto, MedicationEntity.class);
        entity.setMedicationId(UUID.randomUUID().toString());

        when(repository.save(entity)).thenReturn(entity);

//        assertEquals(service.createMedication(dto).getName(), "test");
    }
}