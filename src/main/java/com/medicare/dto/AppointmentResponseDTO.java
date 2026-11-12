package com.medicare.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private String appointmentNumber;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private String departmentName;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String status;
    private String symptoms;
    private String consultationType;
    private String meetingLink;
    private String invoiceNumber;
    private Boolean returningPatient;
    private BigDecimal consultationFee;
    private LocalDateTime createdAt;
    private Long prescriptionId;
}
