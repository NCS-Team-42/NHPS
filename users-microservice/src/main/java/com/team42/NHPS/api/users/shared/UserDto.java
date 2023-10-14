package com.team42.NHPS.api.users.shared;

import com.team42.NHPS.api.users.ui.model.PatientsResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String pharmacyId;
    private String pharmacyName;
    private String encryptedPassword;

}
