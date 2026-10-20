package com.example.mediq.service

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val doctorRepository: DoctorRepository,
) {
    suspend fun findById(id: String): LoadState<Appointment> = appointmentRepository.findById(id)

    suspend fun findAll(filter: AppointmentFilter): LoadState<Paged<Appointment>> = appointmentRepository.findAll(filter)

    suspend fun create(appointment: Appointment): LoadState<Appointment> = appointmentRepository.create(appointment)

    suspend fun update(appointment: Appointment): LoadState<Appointment> = appointmentRepository.update(appointment)

    suspend fun updateStatus(id: String, status: AppointmentStatus, outcome: VisitOutcome? = null): LoadState<Appointment> {
        return appointmentRepository.findById(id).fold(
            onSuccess = { appointment ->
                val updated = appointment.copy(
                    status = status,
                    outcome = outcome,
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                    completedAt = if (status == AppointmentStatus.COMPLETED || status == AppointmentStatus.NO_SHOW) {
                        Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    } else {
                        appointment.completedAt
                    },
                )
                appointmentRepository.update(updated)
            },
            onError = { LoadState.Error(it) },
        )
    }

    suspend fun delete(id: String): LoadState<Unit> = appointmentRepository.delete(id)

    suspend fun checkConflict(doctorId: String, startsAt: LocalDateTime, durationMinutes: Int, excludeId: String? = null): LoadState<Boolean> {
        return appointmentRepository.findConflicts(doctorId, startsAt, durationMinutes, excludeId).fold(
            onSuccess = { LoadState.Success(it.isNotEmpty()) },
            onError = { LoadState.Error(it) },
        )
    }

    suspend fun findForDoctor(doctorId: String, from: LocalDateTime? = null, to: LocalDateTime? = null): LoadState<List<Appointment>> {
        val filter = AppointmentFilter(
            doctorId = doctorId,
            from = from,
            to = to,
            limit = 500,
        )
        return appointmentRepository.findAll(filter).fold(
            onSuccess = { LoadState.Success(it.items) },
            onError = { LoadState.Error(it) },
        )
    }

    suspend fun findForPatient(patientId: String, from: LocalDateTime? = null, to: LocalDateTime? = null): LoadState<List<Appointment>> {
        val filter = AppointmentFilter(
            patientId = patientId,
            from = from,
            to = to,
            limit = 500,
        )
        return appointmentRepository.findAll(filter).fold(
            onSuccess = { LoadState.Success(it.items) },
            onError = { LoadState.Error(it) },
        )
    }
}