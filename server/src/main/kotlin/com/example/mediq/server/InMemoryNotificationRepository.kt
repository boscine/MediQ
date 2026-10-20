package com.example.mediq.server

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import com.example.mediq.util.minusHours
import com.example.mediq.util.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InMemoryNotificationRepository : NotificationRepository {
    private val notifications = MutableStateFlow<Map<String, Notification>>(emptyMap())

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val demoNotifications = listOf(
            Notification("notif-1", "pat-1", NotificationType.APPOINTMENT_BOOKED, "Appointment Booked", "Your appointment with Dr. Smith is booked for tomorrow at 9:00 AM", null, false, now.minusHours(1), null),
            Notification("notif-2", "doc-1", NotificationType.MESSAGE_RECEIVED, "New Message", "You have a new message from John Patient", null, false, now.minusHours(2), null),
        )
        notifications.value = demoNotifications.associateBy { it.id }
    }

    override suspend fun findByUserId(userId: String, unreadOnly: Boolean): LoadState<List<Notification>> {
        var filtered = notifications.value.values.filter { it.userId == userId }
        if (unreadOnly) filtered = filtered.filter { !it.isRead }
        return LoadState.Success(filtered.toList().sortedByDescending { it.createdAt })
    }

    override suspend fun create(notification: Notification): LoadState<Notification> {
        notifications.update { it + (notification.id to notification) }
        return LoadState.Success(notification)
    }

    override suspend fun markAsRead(id: String): LoadState<Unit> {
        notifications.value[id]?.let { notification ->
            notifications.update { it + (id to notification.copy(isRead = true, readAt = Clock.System.now().toLocalDateTime(TimeZone.UTC))) }
        }
        return LoadState.Success(Unit)
    }

    override suspend fun markAllAsRead(userId: String): LoadState<Unit> {
        notifications.update { map ->
            map.mapValues { (_, notification) ->
                if (notification.userId == userId && !notification.isRead) {
                    notification.copy(isRead = true, readAt = Clock.System.now().toLocalDateTime(TimeZone.UTC))
                } else {
                    notification
                }
            }
        }
        return LoadState.Success(Unit)
    }
}
