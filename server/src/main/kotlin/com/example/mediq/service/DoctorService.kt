package com.example.mediq.service

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

class DoctorService(
    private val doctorRepository: DoctorRepository,
    private val specializationRepository: SpecializationRepository,
) {
    suspend fun findById(id: String): LoadState<Doctor> = doctorRepository.findById(id)

    suspend fun findByUserId(userId: String): LoadState<Doctor?> = doctorRepository.findByUserId(userId)

    suspend fun findAll(query: DoctorQuery): LoadState<Paged<Doctor>> = doctorRepository.findAll(query)

    suspend fun create(doctor: Doctor): LoadState<Doctor> = doctorRepository.create(doctor)

    suspend fun update(doctor: Doctor): LoadState<Doctor> = doctorRepository.update(doctor)

    suspend fun updateStatus(id: String, status: DoctorStatus): LoadState<Doctor> = doctorRepository.updateStatus(id, status)

    suspend fun findBySpecialization(specializationId: String): LoadState<List<Doctor>> = doctorRepository.findBySpecialization(specializationId)

    suspend fun getSchedule(doctorId: String): LoadState<List<ClinicHours>> = doctorRepository.getSchedule(doctorId)

    suspend fun saveSchedule(doctorId: String, schedule: List<ClinicHours>): LoadState<Unit> = doctorRepository.saveSchedule(doctorId, schedule)

    suspend fun getAvailability(doctorId: String, date: LocalDate): LoadState<List<AvailableDate>> = doctorRepository.getAvailability(doctorId, date)

    suspend fun saveAvailability(doctorId: String, availability: AvailableDate): LoadState<Unit> = doctorRepository.saveAvailability(doctorId, availability)

    suspend fun getSpecializations(): LoadState<List<Specialty>> = specializationRepository.findAll()

    suspend fun getSpecialization(id: String): LoadState<Specialty> = specializationRepository.findById(id)

    suspend fun createSpecialization(specialty: Specialty): LoadState<Specialty> = specializationRepository.create(specialty)

    suspend fun updateSpecialization(specialty: Specialty): LoadState<Specialty> = specializationRepository.update(specialty)

    suspend fun deleteSpecialization(id: String): LoadState<Unit> = specializationRepository.delete(id)
}