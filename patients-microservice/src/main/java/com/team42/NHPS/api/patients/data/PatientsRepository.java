package com.team42.NHPS.api.patients.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PatientsRepository extends CrudRepository<PatientEntity, Long> {
	List<PatientEntity> findAllByUserId(String userId);
}

