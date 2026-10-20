package com.example.mediq.service

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*

class ProfileService(
    private val profileRepository: ProfileRepository,
) {
    suspend fun findById(id: String): LoadState<UserProfile> = profileRepository.findById(id)

    suspend fun findByUsername(username: String): LoadState<UserProfile?> = profileRepository.findByUsername(username)

    suspend fun update(profile: UserProfile): LoadState<UserProfile> = profileRepository.update(profile)

    suspend fun updateRole(userId: String, role: UserRole): LoadState<UserProfile> = profileRepository.updateRole(userId, role)

    suspend fun updateStatus(userId: String, status: AccountStatus): LoadState<UserProfile> = profileRepository.updateStatus(userId, status)
}