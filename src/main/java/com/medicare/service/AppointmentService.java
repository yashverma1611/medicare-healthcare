package com.medicare.service;

import com.medicare.dto.AppointmentRequestDTO;
import com.medicare.dto.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO bookAppointment(String patientEmail, AppointmentRequestDTO request);
    AppointmentResponseDTO cancelAppointment(Long appointmentId, String userEmail);
    List<AppointmentResponseDTO> getPatientAppointments(String patientEmail);
    List<AppointmentResponseDTO> getDoctorAppointments(String doctorEmail);
    AppointmentResponseDTO getAppointmentById(Long id);
}
