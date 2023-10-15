package com.team42.NHPS.api.patients;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NHPSAppPatientsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NHPSAppPatientsApiApplication.class, args);
	}

	@Bean
	ModelMapper getModelMapper() {
		return new ModelMapper();
	}

}
