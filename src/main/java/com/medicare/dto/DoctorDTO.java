package com.medicare.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private String specialization;
    private String qualification;
    private BigDecimal consultationFee;
    private String roomNumber;
    private String bio;
    private Integer experienceYears;
    private Double rating;
    private String avatarUrl;
}
