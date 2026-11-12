package com.medicare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(nullable = false, length = 100)
    private String medicineName;

    @Column(nullable = false, length = 50)
    private String dosage; // e.g. "500 mg", "10 ml"

    @Column(nullable = false, length = 50)
    private String frequency; // e.g. "1-0-1 (After Food)"

    @Column(nullable = false, length = 50)
    private String duration; // e.g. "5 Days"

    @Column(length = 200)
    private String instructions; // e.g. "Drink plenty of warm water"
}
