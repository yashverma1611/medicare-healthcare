package com.medicare.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.medicare.dto.PrescriptionRequestDTO;
import com.medicare.dto.PrescriptionResponseDTO;
import com.medicare.entity.*;
import com.medicare.exception.BadRequestException;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.AppointmentRepository;
import com.medicare.repository.DoctorRepository;
import com.medicare.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public PrescriptionResponseDTO createPrescription(String doctorEmail, PrescriptionRequestDTO request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.getAppointmentId()));

        Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                .orElseThrow(() -> new BadRequestException("Doctor account not found for: " + doctorEmail));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("Only the assigned consulting doctor can prescribe medications for this appointment.");
        }

        if (prescriptionRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new BadRequestException("Prescription has already been issued for this appointment.");
        }

        Prescription prescription = Prescription.builder()
                .appointment(appointment)
                .doctor(doctor)
                .patient(appointment.getPatient())
                .diagnosis(request.getDiagnosis())
                .clinicalNotes(request.getClinicalNotes())
                .followUpDate(request.getFollowUpDate())
                .build();

        for (PrescriptionRequestDTO.ItemRequestDTO itemDTO : request.getItems()) {
            PrescriptionItem item = PrescriptionItem.builder()
                    .prescription(prescription)
                    .medicineName(itemDTO.getMedicineName())
                    .dosage(itemDTO.getDosage())
                    .frequency(itemDTO.getFrequency())
                    .duration(itemDTO.getDuration())
                    .instructions(itemDTO.getInstructions())
                    .build();
            prescription.getItems().add(item);
        }

        // Mark appointment as COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        Prescription saved = prescriptionRepository.save(prescription);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponseDTO getPrescriptionByAppointmentId(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for appointment: " + appointmentId));
        return mapToDTO(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponseDTO getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "id", prescriptionId));
        return mapToDTO(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generatePrescriptionPdf(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "id", prescriptionId));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);
            document.setMargins(36, 36, 36, 36);

            DeviceRgb primaryNavy = new DeviceRgb(24, 43, 73);
            DeviceRgb tealAccent = new DeviceRgb(13, 148, 136);
            DeviceRgb lightGray = new DeviceRgb(245, 247, 250);

            // 1. Hospital Header
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            Cell clinicCell = new Cell().setBorder(Border.NO_BORDER);
            clinicCell.add(new Paragraph("MEDICARE CLINICAL HOSPITAL")
                    .setFontSize(18).setBold().setFontColor(primaryNavy));
            clinicCell.add(new Paragraph("Department of " + prescription.getDoctor().getDepartment().getName())
                    .setFontSize(10).setFontColor(tealAccent).setBold());
            clinicCell.add(new Paragraph("402 Healthcare Avenue, Gomti Nagar, Lucknow | Helpline: 1800-419-MEDICARE")
                    .setFontSize(8).setFontColor(ColorConstants.GRAY));
            headerTable.addCell(clinicCell);

            Cell rxLogoCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            rxLogoCell.add(new Paragraph("℞")
                    .setFontSize(36).setBold().setFontColor(tealAccent));
            rxLogoCell.add(new Paragraph("Ref: " + prescription.getAppointment().getAppointmentNumber())
                    .setFontSize(9).setFontColor(ColorConstants.DARK_GRAY));
            headerTable.addCell(rxLogoCell);
            document.add(headerTable);

            // Divider Line
            Table divider = new Table(UnitValue.createPercentArray(new float[]{100}));
            divider.setWidth(UnitValue.createPercentValue(100));
            divider.setBorderBottom(new SolidBorder(tealAccent, 2));
            divider.setMarginBottom(12);
            document.add(divider);

            // 2. Doctor & Patient Info Cards
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            infoTable.setWidth(UnitValue.createPercentValue(100));
            infoTable.setBackgroundColor(lightGray);
            infoTable.setPadding(8);

            Cell docCell = new Cell().setBorder(Border.NO_BORDER);
            docCell.add(new Paragraph("CONSULTANT PHYSICIAN").setFontSize(8).setBold().setFontColor(tealAccent));
            docCell.add(new Paragraph(prescription.getDoctor().getUser().getFullName()).setFontSize(12).setBold().setFontColor(primaryNavy));
            docCell.add(new Paragraph(prescription.getDoctor().getQualification() + " (" + prescription.getDoctor().getSpecialization() + ")").setFontSize(9));
            docCell.add(new Paragraph("OPD Room: " + prescription.getDoctor().getRoomNumber()).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY));
            infoTable.addCell(docCell);

            Cell patCell = new Cell().setBorder(Border.NO_BORDER);
            patCell.add(new Paragraph("PATIENT DETAILS").setFontSize(8).setBold().setFontColor(tealAccent));
            patCell.add(new Paragraph(prescription.getPatient().getUser().getFullName()).setFontSize(12).setBold().setFontColor(primaryNavy));
            patCell.add(new Paragraph("Gender: " + prescription.getPatient().getGender() + " | Blood Group: " + prescription.getPatient().getBloodGroup()).setFontSize(9));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
            patCell.add(new Paragraph("Date: " + prescription.getCreatedAt().format(dtf)).setFontSize(9).setFontColor(ColorConstants.DARK_GRAY));
            infoTable.addCell(patCell);
            document.add(infoTable);

            // 3. Clinical Diagnosis
            document.add(new Paragraph("\nCLINICAL DIAGNOSIS").setFontSize(10).setBold().setFontColor(primaryNavy));
            document.add(new Paragraph(prescription.getDiagnosis()).setFontSize(10).setItalic());

            // 4. Prescribed Medications Table
            document.add(new Paragraph("\nPRESCRIBED MEDICINES (Rx)").setFontSize(10).setBold().setFontColor(primaryNavy));
            Table medTable = new Table(UnitValue.createPercentArray(new float[]{8, 32, 20, 20, 20}));
            medTable.setWidth(UnitValue.createPercentValue(100));

            // Table Header
            String[] headers = {"#", "Medicine Name", "Dosage", "Frequency", "Duration"};
            for (String h : headers) {
                Cell hCell = new Cell().setBackgroundColor(primaryNavy).setPadding(6);
                hCell.add(new Paragraph(h).setFontSize(9).setBold().setFontColor(ColorConstants.WHITE));
                medTable.addHeaderCell(hCell);
            }

            int index = 1;
            for (PrescriptionItem item : prescription.getItems()) {
                medTable.addCell(new Cell().setPadding(5).add(new Paragraph(String.valueOf(index++)).setFontSize(9)));
                Cell nameCell = new Cell().setPadding(5);
                nameCell.add(new Paragraph(item.getMedicineName()).setFontSize(9).setBold());
                if (item.getInstructions() != null && !item.getInstructions().isBlank()) {
                    nameCell.add(new Paragraph(item.getInstructions()).setFontSize(7).setItalic().setFontColor(ColorConstants.GRAY));
                }
                medTable.addCell(nameCell);
                medTable.addCell(new Cell().setPadding(5).add(new Paragraph(item.getDosage()).setFontSize(9)));
                medTable.addCell(new Cell().setPadding(5).add(new Paragraph(item.getFrequency()).setFontSize(9)));
                medTable.addCell(new Cell().setPadding(5).add(new Paragraph(item.getDuration()).setFontSize(9)));
            }
            document.add(medTable);

            // 5. Notes & Follow-up
            if (prescription.getClinicalNotes() != null && !prescription.getClinicalNotes().isBlank()) {
                document.add(new Paragraph("\nDOCTOR'S CLINICAL NOTES").setFontSize(10).setBold().setFontColor(primaryNavy));
                document.add(new Paragraph(prescription.getClinicalNotes()).setFontSize(9));
            }

            if (prescription.getFollowUpDate() != null) {
                document.add(new Paragraph("\nRECOMMENDED FOLLOW-UP: " + prescription.getFollowUpDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")))
                        .setFontSize(10).setBold().setFontColor(tealAccent));
            }

            // 6. Signature Block
            document.add(new Paragraph("\n\n"));
            Table signTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}));
            signTable.setWidth(UnitValue.createPercentValue(100));

            Cell disclaimerCell = new Cell().setBorder(Border.NO_BORDER);
            disclaimerCell.add(new Paragraph("Notice: This is a verified digital prescription generated under MediCare Hospital Management Systems. Keep out of reach of children.")
                    .setFontSize(7).setFontColor(ColorConstants.GRAY));
            signTable.addCell(disclaimerCell);

            Cell doctorSignCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            doctorSignCell.add(new Paragraph("Digitally Verified & Signed").setFontSize(9).setBold().setFontColor(tealAccent));
            doctorSignCell.add(new Paragraph(prescription.getDoctor().getUser().getFullName()).setFontSize(10).setBold().setFontColor(primaryNavy));
            doctorSignCell.add(new Paragraph("Reg No: MED-" + prescription.getDoctor().getId() + "981").setFontSize(8).setFontColor(ColorConstants.DARK_GRAY));
            signTable.addCell(doctorSignCell);

            document.add(signTable);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate clinical PDF: {}", e.getMessage(), e);
            throw new BadRequestException("PDF generation failed: " + e.getMessage());
        }
    }

    private PrescriptionResponseDTO mapToDTO(Prescription prescription) {
        List<PrescriptionResponseDTO.ItemResponseDTO> itemDTOs = prescription.getItems().stream()
                .map(item -> PrescriptionResponseDTO.ItemResponseDTO.builder()
                        .id(item.getId())
                        .medicineName(item.getMedicineName())
                        .dosage(item.getDosage())
                        .frequency(item.getFrequency())
                        .duration(item.getDuration())
                        .instructions(item.getInstructions())
                        .build())
                .collect(Collectors.toList());

        return PrescriptionResponseDTO.builder()
                .id(prescription.getId())
                .appointmentId(prescription.getAppointment().getId())
                .appointmentNumber(prescription.getAppointment().getAppointmentNumber())
                .doctorName(prescription.getDoctor().getUser().getFullName())
                .doctorQualification(prescription.getDoctor().getQualification())
                .doctorSpecialization(prescription.getDoctor().getSpecialization())
                .departmentName(prescription.getDoctor().getDepartment().getName())
                .patientName(prescription.getPatient().getUser().getFullName())
                .patientGender(prescription.getPatient().getGender())
                .patientBloodGroup(prescription.getPatient().getBloodGroup())
                .diagnosis(prescription.getDiagnosis())
                .clinicalNotes(prescription.getClinicalNotes())
                .followUpDate(prescription.getFollowUpDate())
                .createdAt(prescription.getCreatedAt())
                .items(itemDTOs)
                .build();
    }
}
