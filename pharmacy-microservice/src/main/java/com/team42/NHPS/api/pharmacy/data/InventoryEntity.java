package com.team42.NHPS.api.pharmacy.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEntity {
    @EmbeddedId
    private PharmacyMedicationKey pharmacyMedicationKey;
    @Column(nullable = false)
    private int quantity = 0;
    @Column(nullable = false)
    private int velocityInWeekly = 0; // Admin set
    @Column(nullable = false)
    private int velocityOutWeekly = 0; // Sum from patient_medicine mapping

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PharmacyMedicationKey {
        private String pharmacyId;
        private String medicationId;
    }
}
