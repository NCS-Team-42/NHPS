package com.team42.NHPS.api.users.ui.model;
 
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseModel {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String pharmacyId;
    private String pharmacyName;

}
