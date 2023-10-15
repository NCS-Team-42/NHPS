package com.team42.NHPS.api.patients.service;

import java.util.List;

import com.team42.NHPS.api.patients.shared.PatientDto;

public interface PatientsService {

	List<PatientDto> getAllPatients();

	PatientDto createPatient(PatientDto patientDto);

	PatientDto getPatientByNric(String nric);

	void deletePatient(String nric);
}
