package com.team42.NHPS.api.pharmacy.service;

import com.team42.NHPS.api.pharmacy.data.PharmacyEntity;
import com.team42.NHPS.api.pharmacy.data.PharmacyRepository;
import com.team42.NHPS.api.pharmacy.exception.ResourceNotFoundException;
import com.team42.NHPS.api.pharmacy.shared.PharmacyDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PharmacyServiceImpl implements PharmacyService {

    private PharmacyRepository pharmacyRepository;
    private ModelMapper modelMapper;

    @Autowired
    public PharmacyServiceImpl(PharmacyRepository pharmacyRepository, ModelMapper modelMapper) {
        this.pharmacyRepository = pharmacyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PharmacyDto getPharmacy(String pharmacyId) {
        PharmacyEntity foundPharmacy = pharmacyRepository.findByPharmacyId(pharmacyId).orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", pharmacyId));
        return modelMapper.map(foundPharmacy, PharmacyDto.class);
    }

    @Override
    public PharmacyDto createPharmacy(PharmacyDto pharmacyDto) {
        PharmacyEntity pharmacyEntity = modelMapper.map(pharmacyDto, PharmacyEntity.class);
        pharmacyEntity.setPharmacyId(UUID.randomUUID().toString());
        PharmacyEntity foundPharmacy = pharmacyRepository.save(pharmacyEntity);
        return modelMapper.map(foundPharmacy, PharmacyDto.class);
    }

    @Override
    public List<PharmacyDto> getAllPharmacy() {
        List<PharmacyDto> pharmacyDtoList = new ArrayList<>();
        Iterable<PharmacyEntity> pharmacyEntityIterator = pharmacyRepository.findAll();
        if (!pharmacyEntityIterator.iterator().hasNext())
            throw new ResourceNotFoundException("Pharmacies", "all", null);
        pharmacyEntityIterator.forEach(pharmacyEntity -> pharmacyDtoList.add(modelMapper.map(pharmacyEntity, PharmacyDto.class)));
        return pharmacyDtoList;
    }
}
