package com.medicare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(nullable = false, length = 100)
    private String qualification; // e.g. MBBS, MD (Cardiology)

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal consultationFee;

    @Column(length = 20)
    private String roomNumber;

    @Column(length = 1000)
    private String bio;

    private Integer experienceYears;

    @Builder.Default
    private Double rating = 4.8;

    private String avatarUrl;

    @Builder.Default
    private Boolean available = true;
}
