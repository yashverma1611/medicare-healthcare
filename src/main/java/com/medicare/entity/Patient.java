package com.medicare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender; // Male, Female, Other

    @Column(length = 5)
    private String bloodGroup; // A+, B+, O+, AB+, etc.

    @Column(length = 20)
    private String emergencyContact;

    @Column(length = 1000)
    private String medicalHistory;
}
