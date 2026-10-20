package com.example.mediq.service

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*

class NotificationService(
    private val notificationRepository: NotificationRepository,
) {
    suspend fun findByUserId(userId: String, unreadOnly: Boolean = false): LoadState<List<Notification>> = notificationRepository.findByUserId(userId, unreadOnly)

    suspend fun create(notification: Notification): LoadState<Notification> = notificationRepository.create(notification)

    suspend fun markAsRead(id: String): LoadState<Unit> = notificationRepository.markAsRead(id)

    suspend fun markAllAsRead(userId: String): LoadState<Unit> = notificationRepository.markAllAsRead(userId)
}