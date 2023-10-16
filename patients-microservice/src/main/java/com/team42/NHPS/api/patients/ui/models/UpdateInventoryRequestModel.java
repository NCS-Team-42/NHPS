package com.team42.NHPS.api.patients.ui.models;

import com.team42.NHPS.api.patients.shared.PrescriptionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryRequestModel {
    private String action;
    private List<PrescriptionDto> prescriptionDtoList;
}
