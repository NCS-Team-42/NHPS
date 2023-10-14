package com.team42.NHPS.api.patients.ui.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team42.NHPS.api.patients.service.PatientsService;
import com.team42.NHPS.api.patients.ui.model.Patient;

import jakarta.validation.Valid;
 

@RestController
@RequestMapping("/patients")
public class PatientsController {
	
	@Autowired
    PatientsService patientsService;
	
	@GetMapping
    @PostAuthorize("(returnObject.size() > 0) ? principal == returnObject[0].userId : true")
	public List<Patient> getPatients(Principal principal) {
		return patientsService.getPatients(principal.getName());
	}

	@PostMapping
	public Patient createPatient(@Valid @RequestBody Patient patient, Principal principal) {
		patient.setUserId(principal.getName());
		return patientsService.createPatient(patient);
	}
 
}
