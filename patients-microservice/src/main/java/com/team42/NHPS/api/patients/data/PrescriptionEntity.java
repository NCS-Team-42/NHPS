package com.team42.NHPS.api.patients.data;


import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionEntity {
    @EmbeddedId
    private PatientMedicationKey patientMedicationKey;
    private String pharmacyId;
    private int consumptionWeekly;
    private int doseLeft;
    private int prescribedDosage;
    private Date prescriptionDate;
    private Date dispenseDate;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientMedicationKey {
        private String patientNric;
        private String medicationId;
    }
}
