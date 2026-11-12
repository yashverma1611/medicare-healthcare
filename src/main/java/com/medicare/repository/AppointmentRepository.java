package com.medicare.repository;

import com.medicare.entity.Appointment;
import com.medicare.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.timeSlot = :slot AND a.status != 'CANCELLED'")
    boolean isSlotBooked(
        @Param("doctorId") Long doctorId,
        @Param("date") LocalDate date,
        @Param("slot") String slot
    );

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate appointmentDate, AppointmentStatus status);

    List<Appointment> findByPatientUserEmailOrderByAppointmentDateDesc(String email);

    List<Appointment> findByDoctorUserEmailOrderByAppointmentDateDesc(String email);

    long countByPatient(com.medicare.entity.Patient patient);
}
