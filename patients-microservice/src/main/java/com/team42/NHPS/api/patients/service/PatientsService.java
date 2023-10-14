package com.team42.NHPS.api.patients.service;

import java.util.List;

import com.team42.NHPS.api.patients.shared.PatientDto;

public interface PatientsService {
	List<PatientDto> getPatients(String userId);
	PatientDto createPatient(PatientDto patientDto);

}
