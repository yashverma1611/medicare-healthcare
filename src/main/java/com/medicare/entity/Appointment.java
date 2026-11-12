package com.medicare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "appointments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_doctor_date_slot", columnNames = {"doctor_id", "appointment_date", "time_slot"})
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "time_slot", nullable = false, length = 20)
    private String timeSlot; // e.g. "09:30 AM - 10:00 AM"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    @Column(length = 500)
    private String symptoms;

    @Column(length = 20)
    @Builder.Default
    private String consultationType = "IN_PERSON"; // "IN_PERSON" or "ONLINE_VIDEO"

    @Column(length = 150)
    private String meetingLink; // Google Meet room link or code

    @Column(precision = 8, scale = 2)
    private java.math.BigDecimal consultationFee;

    @Column(length = 40)
    private String invoiceNumber;

    @Builder.Default
    private Boolean returningPatient = false;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Prescription prescription;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.appointmentNumber == null) {
            this.appointmentNumber = "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.invoiceNumber == null) {
            this.invoiceNumber = "INV-MED-" + LocalDate.now().getYear() + "-" + (int)(Math.random() * 9000 + 1000);
        }
    }
}
