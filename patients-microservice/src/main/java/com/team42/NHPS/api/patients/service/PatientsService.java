package com.team42.NHPS.api.patients.service;

import java.util.List;

import com.team42.NHPS.api.patients.ui.model.Patient;

public interface PatientsService {
	List<Patient> getPatients(String userId);
	Patient createPatient(Patient patient);

}
