package com.team42.NHPS.api.patients.ui.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import com.team42.NHPS.api.patients.service.PatientsService;
import com.team42.NHPS.api.patients.shared.PatientDto;

import jakarta.validation.Valid;
 

@RestController
@RequestMapping("/patients")
public class PatientsController {

	@Value("${token.secret}")
	private String token;
	@Value("${server.port}")
	private String port;
    private PatientsService patientsService;
	private Environment environment;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public PatientsController(PatientsService patientsService, Environment env) {
		this.patientsService = patientsService;
		this.environment = env;
	}

	@GetMapping
    @PostAuthorize("(returnObject.size() > 0) ? principal == returnObject[0].userId : true")
	public List<PatientDto> getPatients(Principal principal) {
		return patientsService.getPatients(principal.getName());
	}

	@PostMapping
	public PatientDto createPatient(@Valid @RequestBody PatientDto patientDto, Principal principal) {
		patientDto.setUserId(principal.getName());
		return patientsService.createPatient(patientDto);
	}

	@GetMapping("/status/check")
	public String status(@RequestHeader("Authorization") String authorizationHeader) {
		String returnValue = "Working on port " + port + " with token " + token + ".\nToken from environment "
				+ environment.getProperty("token.secret") + "\nAuthorizationHeader = " + authorizationHeader
				+ ".\nMy application environment = " + environment.getProperty("myapplication.environment");
		log.info(returnValue);
		return returnValue;
	}

	@GetMapping("/ip")
	public String getIp() {
		String returnValue;

		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
			returnValue = ipAddr.getHostAddress();
		} catch (UnknownHostException ex) {
			returnValue = ex.getLocalizedMessage();
		}

		return returnValue;
	}
 
}
