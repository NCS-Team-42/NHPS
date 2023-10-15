package com.team42.NHPS.api.patients.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface PatientsRepository extends CrudRepository<PatientEntity, Long> {
	Optional<PatientEntity> findByNric(String nric);
}

