package com.example.mediq.server

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import com.example.mediq.util.plusDays
import com.example.mediq.util.plusMinutes
import com.example.mediq.util.update
import com.example.mediq.util.withHour
import com.example.mediq.util.withMinute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InMemoryAppointmentRepository : AppointmentRepository {
    private val appointments = MutableStateFlow<Map<String, Appointment>>(emptyMap())

    init {
        // Seed some demo data
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val demoAppointments = listOf(
            Appointment("apt-1", "pat-1", "doc-1", now.plusDays(1).withHour(9).withMinute(0), 30, AppointmentStatus.BOOKED, "Checkup", now, now),
            Appointment("apt-2", "pat-2", "doc-1", now.plusDays(1).withHour(10).withMinute(0), 30, AppointmentStatus.CONFIRMED, "Follow-up", now, now),
            Appointment("apt-3", "pat-1", "doc-2", now.plusDays(2).withHour(14).withMinute(0), 45, AppointmentStatus.BOOKED, "Consultation", now, now),
        )
        appointments.value = demoAppointments.associateBy { it.id }
    }

    override suspend fun findById(id: String): LoadState<Appointment> = appointments.value[id]?.let { LoadState.Success(it) } ?: LoadState.Error(ApiFailure.notFound("Appointment not found"))

    override suspend fun findAll(filter: AppointmentFilter): LoadState<Paged<Appointment>> {
        var filtered = appointments.value.values.filter { it ->
            (filter.doctorId == null || it.doctorId == filter.doctorId) &&
            (filter.patientId == null || it.patientId == filter.patientId) &&
            (filter.status == null || it.status == filter.status) &&
            (filter.from == null || it.startsAt >= filter.from!!) &&
            (filter.to == null || it.startsAt <= filter.to!!)
        }.sortedBy { it.startsAt }

        val total = filtered.size.toLong()
        filtered = filtered.drop(filter.offset).take(filter.limit)
        return LoadState.Success(Paged(filtered.toList(), total, filter.limit, filter.offset))
    }

    override suspend fun create(appointment: Appointment): LoadState<Appointment> {
        appointments.update { it + (appointment.id to appointment) }
        return LoadState.Success(appointment)
    }

    override suspend fun update(appointment: Appointment): LoadState<Appointment> {
        appointments.update { it + (appointment.id to appointment) }
        return LoadState.Success(appointment)
    }

    override suspend fun updateStatus(id: String, status: AppointmentStatus, outcome: VisitOutcome?): LoadState<Appointment> {
        return appointments.value[id]?.let { appointment ->
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
            appointments.update { it + (id to updated) }
            LoadState.Success(updated)
        } ?: LoadState.Error(ApiFailure.notFound("Appointment not found"))
    }

    override suspend fun delete(id: String): LoadState<Unit> {
        appointments.update { it - id }
        return LoadState.Success(Unit)
    }

    override suspend fun findConflicts(doctorId: String, startsAt: LocalDateTime, durationMinutes: Int, excludeId: String?): LoadState<List<Appointment>> {
        val endTime = startsAt.plusMinutes(durationMinutes)
        val conflicts = appointments.value.values.filter { it ->
            it.doctorId == doctorId &&
            it.id != excludeId &&
            it.status != AppointmentStatus.CANCELLED &&
            it.status != AppointmentStatus.NO_SHOW &&
            it.status != AppointmentStatus.COMPLETED &&
            it.startsAt < endTime &&
            it.startsAt.plusMinutes(it.durationMinutes) > startsAt
        }.toList()
        return LoadState.Success(conflicts)
    }
}
