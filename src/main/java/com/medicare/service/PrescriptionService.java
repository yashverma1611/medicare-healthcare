package com.medicare.service;

import com.medicare.dto.PrescriptionRequestDTO;
import com.medicare.dto.PrescriptionResponseDTO;

public interface PrescriptionService {
    PrescriptionResponseDTO createPrescription(String doctorEmail, PrescriptionRequestDTO request);
    PrescriptionResponseDTO getPrescriptionByAppointmentId(Long appointmentId);
    PrescriptionResponseDTO getPrescriptionById(Long prescriptionId);
    byte[] generatePrescriptionPdf(Long prescriptionId);
}
