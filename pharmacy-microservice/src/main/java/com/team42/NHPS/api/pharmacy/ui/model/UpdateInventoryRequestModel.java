package com.team42.NHPS.api.pharmacy.ui.model;

import com.team42.NHPS.api.pharmacy.shared.PrescriptionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryRequestModel {
    private String action;
    private PrescriptionDto prescriptionDto;
    private int dispenseQuantity;
}
