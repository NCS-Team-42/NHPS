package com.team42.NHPS.api.patients.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true)
	private String nric;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "date_of_birth", nullable = false)
	private Date dob;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "postal_code", nullable = false)
	private String postalCode;
}