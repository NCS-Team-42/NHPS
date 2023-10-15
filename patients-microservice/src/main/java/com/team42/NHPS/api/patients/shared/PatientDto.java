package com.team42.NHPS.api.patients.shared;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {

	@NonNull
	@Pattern(regexp = "^[A-Z][0-9]{7}[A-Z]$")
	private String nric;

	@NonNull
	private String firstName;

	@NonNull
	private String lastName;

	@NonNull
	@Email
	private String email;

	@NonNull
	private Date dob;

	@NonNull
	@Pattern(regexp = "^[0-9]{8,12}$", message = "Phone number must be 8 to 12 length with no special characters")
	private String phoneNumber;

	@NonNull
	private String postalCode;

}
