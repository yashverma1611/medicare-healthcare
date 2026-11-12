package com.medicare.controller;

import com.medicare.dto.AppointmentRequestDTO;
import com.medicare.dto.AppointmentResponseDTO;
import com.medicare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            Authentication authentication,
            @Valid @RequestBody AppointmentRequestDTO request
    ) {
        String patientEmail = authentication.getName();
        return new ResponseEntity<>(appointmentService.bookAppointment(patientEmail, request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, userEmail));
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(Authentication authentication) {
        String patientEmail = authentication.getName();
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientEmail));
    }

    @GetMapping("/doctor-schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponseDTO>> getDoctorSchedule(Authentication authentication) {
        String doctorEmail = authentication.getName();
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }
}
