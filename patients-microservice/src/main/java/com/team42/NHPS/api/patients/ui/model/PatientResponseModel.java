package com.team42.NHPS.api.patients.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseModel {
	private long id;

	private String userId;

	private String nric;

	private String firstName;

	private String lastName;

	private String email;

	private Date dob;

	private String phoneNumber;

}
