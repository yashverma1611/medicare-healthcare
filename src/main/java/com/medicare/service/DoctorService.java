package com.medicare.service;

import com.medicare.dto.DoctorDTO;
import com.medicare.dto.SlotDTO;

import java.time.LocalDate;
import java.util.List;

public interface DoctorService {
    List<DoctorDTO> getAllDoctors();
    List<DoctorDTO> getDoctorsByDepartment(Long departmentId);
    DoctorDTO getDoctorById(Long id);
    List<SlotDTO> getAvailableSlots(Long doctorId, LocalDate date);
}
