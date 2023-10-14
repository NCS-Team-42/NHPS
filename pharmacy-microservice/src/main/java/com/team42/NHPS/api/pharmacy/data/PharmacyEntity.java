package com.team42.NHPS.api.pharmacy.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pharmacy")
public class PharmacyEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "pharmacy_id", nullable = false, unique = true)
    private String pharmacyId;

    @Column(name = "pharmacy_name", nullable = false, unique = true)
    private String pharmacyName;

    @Column(name = "postal_code", nullable = false, unique = true)
    private String postalCode;
}
