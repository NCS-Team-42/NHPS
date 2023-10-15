package com.team42.NHPS.api.pharmacy.data;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "pharmacy_user_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyUserMappingEntity {
    @EmbeddedId
    PharmacyUserMappingId pharmacyUserMappingId;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PharmacyUserMappingId implements Serializable {
        private String pharmacyId;
        private String userId;
    }
}
