package com.medicare.config;

import com.medicare.entity.*;
import com.medicare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("MediCare database already initialized.");
            return;
        }

        log.info("Bootstrapping MediCare clinical hospital records...");

        // 1. Roles
        Role adminRole = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        Role doctorRole = roleRepository.save(Role.builder().name("ROLE_DOCTOR").build());
        Role patientRole = roleRepository.save(Role.builder().name("ROLE_PATIENT").build());

        // 2. Users & Profiles
        // Admin
        User admin = User.builder()
                .email("admin@medicare.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Hospital Administrator")
                .phone("+91 98765 00001")
                .roles(Set.of(adminRole, doctorRole))
                .build();
        userRepository.save(admin);

        // Departments
        Department cardiology = departmentRepository.save(Department.builder()
                .name("Cardiology")
                .description("Heart disease diagnostics, coronary angioplasty, and cardiovascular care.")
                .iconUrl("fa-heart-pulse")
                .build());

        Department orthopedics = departmentRepository.save(Department.builder()
                .name("Orthopedics")
                .description("Bone fractures, joint replacements, arthritis treatment and trauma care.")
                .iconUrl("fa-bone")
                .build());

        Department pediatrics = departmentRepository.save(Department.builder()
                .name("Pediatrics")
                .description("Neonatal, child healthcare, growth monitoring and vaccinations.")
                .iconUrl("fa-baby")
                .build());

        Department neurology = departmentRepository.save(Department.builder()
                .name("Neurology")
                .description("Brain, spinal cord, migraine and neuro-muscular diagnostics.")
                .iconUrl("fa-brain")
                .build());

        Department dermatology = departmentRepository.save(Department.builder()
                .name("Dermatology")
                .description("Skin conditions, acne, eczema, psoriasis, and cosmetic dermatology.")
                .iconUrl("fa-hand-dots")
                .build());

        Department gastroenterology = departmentRepository.save(Department.builder()
                .name("Gastroenterology")
                .description("Digestive tract, liver health, endoscopy, and inflammatory bowel diseases.")
                .iconUrl("fa-capsules")
                .build());

        Department oncology = departmentRepository.save(Department.builder()
                .name("Oncology")
                .description("Cancer diagnosis, systemic chemotherapy, immunotherapy and palliative care.")
                .iconUrl("fa-ribbon")
                .build());

        Department gynecology = departmentRepository.save(Department.builder()
                .name("Gynecology")
                .description("Obstetrics, maternal-fetal care, hormonal health and laparoscopic surgery.")
                .iconUrl("fa-person-pregnant")
                .build());

        Department ophthalmology = departmentRepository.save(Department.builder()
                .name("Ophthalmology")
                .description("Cataract surgery, glaucoma, diabetic retinopathy, and laser vision correction.")
                .iconUrl("fa-eye")
                .build());

        Department ent = departmentRepository.save(Department.builder()
                .name("ENT")
                .description("Ear, nose, throat, sinusitis, vertigo, and vocal cord disorders.")
                .iconUrl("fa-head-side-cough")
                .build());

        Department generalMedicine = departmentRepository.save(Department.builder()
                .name("General Medicine")
                .description("Internal medicine, lifestyle diseases, hypertension and chronic disease management.")
                .iconUrl("fa-user-doctor")
                .build());

        // Doctors
        User docUser1 = userRepository.save(User.builder()
                .email("priya.sharma@medicare.com")
                .password(passwordEncoder.encode("doctor123"))
                .fullName("Dr. Priya Sharma")
                .phone("+91 98765 43210")
                .roles(Collections.singleton(doctorRole))
                .build());

        Doctor doc1 = doctorRepository.save(Doctor.builder()
                .user(docUser1)
                .department(cardiology)
                .specialization("Interventional Cardiology")
                .qualification("MBBS, MD, DM (AIIMS Delhi)")
                .consultationFee(new BigDecimal("800.00"))
                .roomNumber("OPD-104")
                .bio("12+ years of senior clinical experience in coronary intervention, hypertension and non-invasive echo.")
                .experienceYears(12)
                .rating(4.9)
                .avatarUrl("https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=300&q=80")
                .available(true)
                .build());

        User docUser2 = userRepository.save(User.builder()
                .email("rajesh.verma@medicare.com")
                .password(passwordEncoder.encode("doctor123"))
                .fullName("Dr. Rajesh Verma")
                .phone("+91 98765 43211")
                .roles(Collections.singleton(doctorRole))
                .build());

        Doctor doc2 = doctorRepository.save(Doctor.builder()
                .user(docUser2)
                .department(orthopedics)
                .specialization("Joint Replacement & Arthroscopy")
                .qualification("MBBS, MS (Ortho - KGMU)")
                .consultationFee(new BigDecimal("700.00"))
                .roomNumber("OPD-208")
                .bio("15+ years of surgical experience in knee replacement, spine deformity, and sports injury recovery.")
                .experienceYears(15)
                .rating(4.8)
                .avatarUrl("https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=300&q=80")
                .available(true)
                .build());

        User docUser3 = userRepository.save(User.builder()
                .email("ananya.sen@medicare.com")
                .password(passwordEncoder.encode("doctor123"))
                .fullName("Dr. Ananya Sen")
                .phone("+91 98765 43212")
                .roles(Collections.singleton(doctorRole))
                .build());

        Doctor doc3 = doctorRepository.save(Doctor.builder()
                .user(docUser3)
                .department(pediatrics)
                .specialization("Pediatric Critical Care")
                .qualification("MBBS, MD (Pediatrics)")
                .consultationFee(new BigDecimal("600.00"))
                .roomNumber("OPD-112")
                .bio("Specialist in pediatric pulmonary infections, childhood allergy treatments, and developmental milestones.")
                .experienceYears(9)
                .rating(4.95)
                .avatarUrl("https://images.unsplash.com/photo-1594824813511-236b2b7811fa?auto=format&fit=crop&w=300&q=80")
                .available(true)
                .build());

        // Patients
        User patUser1 = userRepository.save(User.builder()
                .email("tanupriya@medicare.com")
                .password(passwordEncoder.encode("patient123"))
                .fullName("Tanupriya Rai")
                .phone("+91 98765 99001")
                .roles(Collections.singleton(patientRole))
                .build());

        Patient pat1 = patientRepository.save(Patient.builder()
                .user(patUser1)
                .dateOfBirth(LocalDate.of(2003, 5, 14))
                .gender("Female")
                .bloodGroup("B+")
                .emergencyContact("+91 98765 99000")
                .medicalHistory("Seasonal allergic rhinitis, No known chronic hypertension.")
                .build());

        // Seed Appointments
        Appointment appt1 = appointmentRepository.save(Appointment.builder()
                .patient(pat1)
                .doctor(doc1)
                .appointmentDate(LocalDate.now().plusDays(1))
                .timeSlot("10:00 AM - 10:30 AM")
                .status(AppointmentStatus.CONFIRMED)
                .symptoms("Mild chest discomfort after strenuous workout and irregular palpitations.")
                .build());

        Appointment apptCompleted = appointmentRepository.save(Appointment.builder()
                .patient(pat1)
                .doctor(doc1)
                .appointmentDate(LocalDate.now().minusDays(3))
                .timeSlot("11:00 AM - 11:30 AM")
                .status(AppointmentStatus.COMPLETED)
                .symptoms("Acute chest burning sensation, acid reflux, post-prandial heaviness.")
                .build());

        // Seed Prescription for completed appointment
        Prescription pres = Prescription.builder()
                .appointment(apptCompleted)
                .doctor(doc1)
                .patient(pat1)
                .diagnosis("Gastroesophageal Reflux Disease (GERD) with mild sinus tachycardia")
                .clinicalNotes("ECG shows normal sinus rhythm. Patient advised to reduce caffeine intake, avoid lying down immediately after meals.")
                .followUpDate(LocalDate.now().plusWeeks(2))
                .build();

        pres.getItems().addAll(List.of(
                PrescriptionItem.builder()
                        .prescription(pres)
                        .medicineName("Pantoprazole 40mg")
                        .dosage("1 Tablet")
                        .frequency("1-0-0 (Empty stomach before breakfast)")
                        .duration("14 Days")
                        .instructions("Swallow whole with glass of water")
                        .build(),
                PrescriptionItem.builder()
                        .prescription(pres)
                        .medicineName("Metoprolol Tartrate 25mg")
                        .dosage("1 Tablet")
                        .frequency("0-0-1 (After dinner)")
                        .duration("10 Days")
                        .instructions("Monitor resting pulse rate daily")
                        .build(),
                PrescriptionItem.builder()
                        .prescription(pres)
                        .medicineName("Gelusil MPS Antacid Syrup")
                        .dosage("10 ml")
                        .frequency("1-1-1 (15 mins after meals)")
                        .duration("7 Days")
                        .instructions("Shake bottle well before use")
                        .build()
        ));

        prescriptionRepository.save(pres);
        apptCompleted.setPrescription(pres);
        appointmentRepository.save(apptCompleted);

        log.info("MediCare data initialization complete! Seeded 3 doctors, 4 departments, 1 patient and 1 verified prescription.");
    }
}
