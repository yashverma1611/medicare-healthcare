package com.medicare.service;

import com.medicare.dto.AppointmentRequestDTO;
import com.medicare.dto.AppointmentResponseDTO;
import com.medicare.entity.*;
import com.medicare.exception.BadRequestException;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.exception.SlotAlreadyBookedException;
import com.medicare.repository.AppointmentRepository;
import com.medicare.repository.DoctorRepository;
import com.medicare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public AppointmentResponseDTO bookAppointment(String patientEmail, AppointmentRequestDTO request) {
        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book appointment for a past date: " + request.getAppointmentDate());
        }

        Patient patient = patientRepository.findByUserEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for: " + patientEmail));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        if (!doctor.getAvailable()) {
            throw new BadRequestException("Doctor is currently not available for bookings");
        }

        // Check if slot is already reserved
        if (appointmentRepository.isSlotBooked(doctor.getId(), request.getAppointmentDate(), request.getTimeSlot())) {
            throw new SlotAlreadyBookedException(String.format(
                    "Selected slot '%s' on %s is already reserved. Please select another slot.",
                    request.getTimeSlot(), request.getAppointmentDate()
            ));
        }

        boolean isReturning = appointmentRepository.countByPatient(patient) > 0;
        String type = (request.getConsultationType() != null && !request.getConsultationType().isBlank())
                ? request.getConsultationType() : "IN_PERSON";
        String meet = type.equalsIgnoreCase("ONLINE_VIDEO")
                ? "meet.google.com/med-" + java.util.UUID.randomUUID().toString().substring(0, 7)
                : null;

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .timeSlot(request.getTimeSlot())
                .symptoms(request.getSymptoms())
                .status(AppointmentStatus.BOOKED)
                .consultationType(type)
                .meetingLink(meet)
                .consultationFee(doctor.getConsultationFee())
                .returningPatient(isReturning)
                .build();

        try {
            Appointment saved = appointmentRepository.save(appointment);
            return mapToDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new SlotAlreadyBookedException("Slot concurrency conflict: Appointment was just booked by another patient.");
        }
    }

    @Override
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long appointmentId, String userEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        boolean isPatient = appointment.getPatient().getUser().getEmail().equals(userEmail);
        boolean isDoctor = appointment.getDoctor().getUser().getEmail().equals(userEmail);

        if (!isPatient && !isDoctor) {
            throw new BadRequestException("Unauthorized to cancel this appointment");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Completed appointments cannot be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getPatientAppointments(String patientEmail) {
        return appointmentRepository.findByPatientUserEmailOrderByAppointmentDateDesc(patientEmail).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getDoctorAppointments(String doctorEmail) {
        return appointmentRepository.findByDoctorUserEmailOrderByAppointmentDateDesc(doctorEmail).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        return mapToDTO(appointment);
    }

    private AppointmentResponseDTO mapToDTO(Appointment appt) {
        return AppointmentResponseDTO.builder()
                .id(appt.getId())
                .appointmentNumber(appt.getAppointmentNumber())
                .patientId(appt.getPatient().getId())
                .patientName(appt.getPatient().getUser().getFullName())
                .patientEmail(appt.getPatient().getUser().getEmail())
                .doctorId(appt.getDoctor().getId())
                .doctorName(appt.getDoctor().getUser().getFullName())
                .doctorSpecialization(appt.getDoctor().getSpecialization())
                .departmentName(appt.getDoctor().getDepartment().getName())
                .appointmentDate(appt.getAppointmentDate())
                .timeSlot(appt.getTimeSlot())
                .status(appt.getStatus().name())
                .symptoms(appt.getSymptoms())
                .consultationType(appt.getConsultationType())
                .meetingLink(appt.getMeetingLink())
                .invoiceNumber(appt.getInvoiceNumber())
                .returningPatient(appt.getReturningPatient())
                .consultationFee(appt.getConsultationFee() != null ? appt.getConsultationFee() : appt.getDoctor().getConsultationFee())
                .createdAt(appt.getCreatedAt())
                .prescriptionId(appt.getPrescription() != null ? appt.getPrescription().getId() : null)
                .build();
    }
}
