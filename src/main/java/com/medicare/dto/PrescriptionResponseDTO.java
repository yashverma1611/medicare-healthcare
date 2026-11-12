package com.medicare.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseDTO {
    private Long id;
    private Long appointmentId;
    private String appointmentNumber;
    private String doctorName;
    private String doctorQualification;
    private String doctorSpecialization;
    private String departmentName;
    private String patientName;
    private String patientGender;
    private String patientBloodGroup;
    private String diagnosis;
    private String clinicalNotes;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;
    private List<ItemResponseDTO> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResponseDTO {
        private Long id;
        private String medicineName;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}
