package com.example.mediq.server

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import com.example.mediq.util.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InMemoryDoctorRepository : DoctorRepository {
    internal val doctors = MutableStateFlow<Map<String, Doctor>>(emptyMap())
    private val schedules = MutableStateFlow<Map<String, List<ClinicHours>>>(emptyMap())
    private val availabilities = MutableStateFlow<Map<String, List<AvailableDate>>>(emptyMap())

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val demoDoctors = listOf(
            Doctor(
                id = "doc-1",
                userId = "doc-1",
                fullName = "Dr. Sarah Smith",
                specialty = "Cardiology",
                specializationId = "cardiology",
                licenseNumber = "PHYS-12345",
                yearsOfExperience = 15,
                feeCentavos = 150000, // PHP 1,500
                bio = "Board-certified cardiologist with 15 years experience in interventional cardiology.",
                location = ClinicLocation("MediQ Medical Center", "3", "301"),
                languages = listOf(ConsultationLanguage("en", "English"), ConsultationLanguage("tl", "Tagalog")),
                status = DoctorStatus.ACTIVE,
                createdAt = now,
                updatedAt = now,
            ),
            Doctor(
                id = "doc-2",
                userId = "doc-2",
                fullName = "Dr. Michael Chen",
                specialty = "Dermatology",
                specializationId = "dermatology",
                licenseNumber = "PHYS-23456",
                yearsOfExperience = 12,
                feeCentavos = 120000,
                bio = "Dermatologist specializing in medical and cosmetic dermatology.",
                location = ClinicLocation("MediQ Medical Center", "2", "205"),
                languages = listOf(ConsultationLanguage("en", "English"), ConsultationLanguage("zh", "Chinese")),
                status = DoctorStatus.ACTIVE,
                createdAt = now,
                updatedAt = now,
            ),
            Doctor(
                id = "doc-3",
                userId = "doc-3",
                fullName = "Dr. Maria Garcia",
                specialty = "Pediatrics",
                specializationId = "pediatrics",
                licenseNumber = "PHYS-34567",
                yearsOfExperience = 10,
                feeCentavos = 100000,
                bio = "Pediatrician with focus on developmental pediatrics.",
                location = ClinicLocation("MediQ Medical Center", "1", "110"),
                languages = listOf(ConsultationLanguage("en", "English"), ConsultationLanguage("tl", "Tagalog"), ConsultationLanguage("es", "Spanish")),
                status = DoctorStatus.ACTIVE,
                createdAt = now,
                updatedAt = now,
            ),
        )
        doctors.value = demoDoctors.associateBy { it.id }

        // Default schedules (Monday-Friday 9-17)
        schedules.value = demoDoctors.associate { doc ->
            doc.id to (1..5).map { day ->
                ClinicHours(day, "09:00", "17:00")
            }
        }
    }

    override suspend fun findById(id: String): LoadState<Doctor> = doctors.value[id]?.let { LoadState.Success(it) } ?: LoadState.Error(ApiFailure.notFound("Doctor not found"))

    override suspend fun findByUserId(userId: String): LoadState<Doctor?> = LoadState.Success(doctors.value.values.find { it.userId == userId })

    override suspend fun findAll(query: DoctorQuery): LoadState<Paged<Doctor>> {
        var filtered = doctors.value.values.filter { it ->
            (query.specializationId == null || it.specializationId == query.specializationId) &&
            (query.search == null || it.fullName.contains(query.search!!, ignoreCase = true) || it.specialty.contains(query.search!!, ignoreCase = true)) &&
            (query.status == null || it.status == query.status)
        }.sortedBy { it.fullName }

        val total = filtered.size.toLong()
        filtered = filtered.drop(query.offset).take(query.limit)
        return LoadState.Success(Paged(filtered.toList(), total, query.limit, query.offset))
    }

    override suspend fun create(doctor: Doctor): LoadState<Doctor> {
        doctors.update { it + (doctor.id to doctor) }
        return LoadState.Success(doctor)
    }

    override suspend fun update(doctor: Doctor): LoadState<Doctor> {
        doctors.update { it + (doctor.id to doctor.copy(updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC))) }
        return LoadState.Success(doctor)
    }

    override suspend fun updateStatus(id: String, status: DoctorStatus): LoadState<Doctor> {
        return doctors.value[id]?.let { doctor ->
            val updated = doctor.copy(status = status, updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC))
            doctors.update { it + (id to updated) }
            LoadState.Success(updated)
        } ?: LoadState.Error(ApiFailure.notFound("Doctor not found"))
    }

    override suspend fun findBySpecialization(specializationId: String): LoadState<List<Doctor>> {
        return LoadState.Success(doctors.value.values.filter { it.specializationId == specializationId }.toList())
    }

    override suspend fun getSchedule(doctorId: String): LoadState<List<ClinicHours>> = LoadState.Success(schedules.value[doctorId] ?: emptyList())

    override suspend fun saveSchedule(doctorId: String, schedule: List<ClinicHours>): LoadState<Unit> {
        schedules.update { it + (doctorId to schedule) }
        return LoadState.Success(Unit)
    }

    override suspend fun getAvailability(doctorId: String, date: kotlinx.datetime.LocalDate): LoadState<List<AvailableDate>> {
        return LoadState.Success(availabilities.value[doctorId]?.filter { it.date == date } ?: emptyList())
    }

    override suspend fun saveAvailability(doctorId: String, availability: AvailableDate): LoadState<Unit> {
        availabilities.update { map ->
            val current = map[doctorId] ?: emptyList()
            val updated = current.filter { it.date != availability.date } + availability
            map + (doctorId to updated)
        }
        return LoadState.Success(Unit)
    }
}