package com.example.mediq.server

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import com.example.mediq.util.minusDays
import com.example.mediq.util.minusHours
import com.example.mediq.util.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class InMemoryMessageRepository : MessageRepository {
    private val conversations = MutableStateFlow<Map<String, MessageThread>>(emptyMap())
    private val messages = MutableStateFlow<Map<String, MutableList<Message>>>(emptyMap())

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val demoConversations = listOf(
            MessageThread("conv-1", "pat-1", "doc-1", "Follow-up question", now.minusHours(2), 1, false),
            MessageThread("conv-2", "pat-2", "doc-1", "Prescription refill", now.minusDays(1), 0, true),
        )
        conversations.value = demoConversations.associateBy { it.id }
        messages.value = mutableMapOf(
            "conv-1" to mutableListOf(
                Message("msg-1", "conv-1", "pat-1", MessageAuthorRole.PATIENT, "Hi Dr. Smith, I have a question about my medication.", now.minusHours(3)),
                Message("msg-2", "conv-1", "doc-1", MessageAuthorRole.DOCTOR, "Sure, what's your question?", now.minusHours(2)),
            ),
            "conv-2" to mutableListOf(
                Message("msg-3", "conv-2", "pat-2", MessageAuthorRole.PATIENT, "Can I get a refill on my prescription?", now.minusDays(1)),
            ),
        )
    }

    override suspend fun findConversation(id: String): LoadState<MessageThread?> = LoadState.Success(conversations.value[id])

    override suspend fun findConversationsForDoctor(doctorId: String): LoadState<List<MessageThread>> {
        return LoadState.Success(conversations.value.values.filter { it.doctorId == doctorId }.toList())
    }

    override suspend fun findConversationsForPatient(patientId: String): LoadState<List<MessageThread>> {
        return LoadState.Success(conversations.value.values.filter { it.patientId == patientId }.toList())
    }

    override suspend fun findConversationsForSecretary(secretaryId: String): LoadState<List<MessageThread>> {
        // Secretary sees conversations for their assigned doctor
        return LoadState.Success(emptyList())
    }

    override suspend fun findMessages(conversationId: String): LoadState<List<Message>> {
        return LoadState.Success(messages.value[conversationId]?.toList() ?: emptyList())
    }

    override suspend fun createMessage(message: Message): LoadState<Message> {
        messages.update { map ->
            val list = map[message.conversationId] ?: mutableListOf()
            list.add(message)
            map + (message.conversationId to list)
        }
        return LoadState.Success(message)
    }

    override suspend fun markAsRead(conversationId: String, userId: String): LoadState<Unit> {
        conversations.update { map ->
            val thread = map[conversationId] ?: return@update map
            map + (conversationId to thread.copy(unreadCount = 0))
        }
        return LoadState.Success(Unit)
    }

    override suspend fun createConversation(thread: MessageThread): LoadState<MessageThread> {
        conversations.update { it + (thread.id to thread) }
        messages.update { it + (thread.id to mutableListOf()) }
        return LoadState.Success(thread)
    }
}