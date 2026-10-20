package com.example.mediq.server

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import com.example.mediq.util.update
import kotlinx.coroutines.flow.MutableStateFlow

class InMemorySpecializationRepository : SpecializationRepository {
    private val specialties = MutableStateFlow<Map<String, Specialty>>(emptyMap())

    init {
        val demoSpecialties = listOf(
            Specialty("cardiology", "Cardiology", "Heart and cardiovascular system", 1),
            Specialty("dermatology", "Dermatology", "Skin, hair, and nails", 1),
            Specialty("pediatrics", "Pediatrics", "Children's health", 1),
            Specialty("orthopedics", "Orthopedics", "Bones, joints, and muscles", 0),
            Specialty("neurology", "Neurology", "Nervous system disorders", 0),
            Specialty("psychiatry", "Psychiatry", "Mental health", 0),
        )
        specialties.value = demoSpecialties.associateBy { it.id }
    }

    override suspend fun findAll(): LoadState<List<Specialty>> = LoadState.Success(specialties.value.values.toList())

    override suspend fun findById(id: String): LoadState<Specialty> = specialties.value[id]?.let { LoadState.Success(it) } ?: LoadState.Error(ApiFailure.notFound("Specialization not found"))

    override suspend fun create(specialty: Specialty): LoadState<Specialty> {
        specialties.update { it + (specialty.id to specialty) }
        return LoadState.Success(specialty)
    }

    override suspend fun update(specialty: Specialty): LoadState<Specialty> {
        specialties.update { it + (specialty.id to specialty) }
        return LoadState.Success(specialty)
    }

    override suspend fun delete(id: String): LoadState<Unit> {
        specialties.update { it - id }
        return LoadState.Success(Unit)
    }
}