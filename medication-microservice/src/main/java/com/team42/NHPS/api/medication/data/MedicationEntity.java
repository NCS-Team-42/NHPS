package com.team42.NHPS.api.medication.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medication")
public class MedicationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "medication_id", nullable = false, unique = true)
    private String medicationId;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "description", nullable = false)
    private String description; // could include side effects
    @Column(name = "active_ingredient", nullable = false)
    private String activeIngredient; // eg paracetamol
    @Column(name = "dosage", nullable = false)
    private String dosage; // eg 3mg
    @Column(name = "instructions", nullable = false)
    private String instructions; // eg 30 min before food
}
