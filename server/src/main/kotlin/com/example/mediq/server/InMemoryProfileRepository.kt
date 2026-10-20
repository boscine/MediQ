package com.example.mediq.server

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import com.example.mediq.util.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InMemoryProfileRepository : ProfileRepository {
    private val profiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val demoProfiles = listOf(
            UserProfile("doc-1", "dr.smith", "Dr. Sarah Smith", "dr.smith@mediq.app", "+639171234567", kotlinx.datetime.LocalDate.parse("1980-05-15"), UserRole.DOCTOR, AccountStatus.ACTIVE, doctorId = "doc-1", createdAt = now, updatedAt = now),
            UserProfile("doc-2", "dr.chen", "Dr. Michael Chen", "dr.chen@mediq.app", "+639172345678", kotlinx.datetime.LocalDate.parse("1975-11-22"), UserRole.DOCTOR, AccountStatus.ACTIVE, doctorId = "doc-2", createdAt = now, updatedAt = now),
            UserProfile("sec-1", "sec.jones", "Secretary Jane Jones", "sec.jones@mediq.app", "+639173456789", kotlinx.datetime.LocalDate.parse("1990-03-10"), UserRole.SECRETARY, AccountStatus.ACTIVE, assignedDoctorId = "doc-1", createdAt = now, updatedAt = now),
            UserProfile("admin-1", "admin", "Admin User", "admin@mediq.app", "+639170000000", kotlinx.datetime.LocalDate.parse("1985-01-01"), UserRole.ADMIN, AccountStatus.ACTIVE, createdAt = now, updatedAt = now),
            UserProfile("pat-1", "demo_patient", "Demo Patient", "demo@patient.com", "+639181111111", kotlinx.datetime.LocalDate.parse("1995-07-20"), UserRole.PATIENT, AccountStatus.ACTIVE, createdAt = now, updatedAt = now),
            UserProfile("pat-2", "demo_patient2", "Maria Santos", "maria@patient.com", "+639182222222", kotlinx.datetime.LocalDate.parse("1988-12-05"), UserRole.PATIENT, AccountStatus.ACTIVE, createdAt = now, updatedAt = now),
        )
        profiles.value = demoProfiles.associateBy { it.id }
    }

    override suspend fun findById(id: String): LoadState<UserProfile> = profiles.value[id]?.let { LoadState.Success(it) } ?: LoadState.Error(ApiFailure.notFound("Profile not found"))

    override suspend fun findByUsername(username: String): LoadState<UserProfile?> = LoadState.Success(profiles.value.values.find { it.username == username })

    override suspend fun update(profile: UserProfile): LoadState<UserProfile> {
        profiles.update { it + (profile.id to profile.copy(updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC))) }
        return LoadState.Success(profile)
    }

    override suspend fun updateRole(userId: String, role: UserRole): LoadState<UserProfile> {
        return profiles.value[userId]?.let { profile ->
            val updated = profile.copy(role = role, updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC))
            profiles.update { it + (userId to updated) }
            LoadState.Success(updated)
        } ?: LoadState.Error(ApiFailure.notFound("Profile not found"))
    }

    override suspend fun updateStatus(userId: String, status: AccountStatus): LoadState<UserProfile> {
        return profiles.value[userId]?.let { profile ->
            val updated = profile.copy(status = status, updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC))
            profiles.update { it + (userId to updated) }
            LoadState.Success(updated)
        } ?: LoadState.Error(ApiFailure.notFound("Profile not found"))
    }
}