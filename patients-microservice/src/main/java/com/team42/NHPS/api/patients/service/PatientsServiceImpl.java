package com.team42.NHPS.api.patients.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.team42.NHPS.api.patients.data.PatientEntity;
import com.team42.NHPS.api.patients.data.PatientsRepository;
import com.team42.NHPS.api.patients.ui.model.Patient;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientsServiceImpl implements PatientsService {
	
	@Autowired
    PatientsRepository patientsRepository;

	@Override
	public List<Patient> getPatients(String userId) {
		
		List<PatientEntity> albumEntities = (List<PatientEntity>) patientsRepository.findAllByUserId(userId);

		if (albumEntities == null || albumEntities.isEmpty())
			return new ArrayList<>();

		Type listType = new TypeToken<List<Patient>>() {
		}.getType();

		return new ModelMapper().map(albumEntities, listType);
	}

	@Override
	public Patient createPatient(Patient patient) {
		patient.setAlbumId(UUID.randomUUID().toString());
		
		PatientEntity patientEntity = new PatientEntity();
		BeanUtils.copyProperties(patient, patientEntity);
		
		PatientEntity storedPatientEntity = patientsRepository.save(patientEntity);
		
		Patient returnValue = new Patient();
		BeanUtils.copyProperties(storedPatientEntity, returnValue);
		
		return returnValue;
	}

}
