package com.example.mediq.service

import com.example.mediq.domain.model.*
import com.example.mediq.domain.repository.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

class MessageService(
    private val messageRepository: MessageRepository,
) {
    suspend fun findConversation(id: String): LoadState<MessageThread?> = messageRepository.findConversation(id)

    suspend fun findConversationsForDoctor(doctorId: String): LoadState<List<MessageThread>> = messageRepository.findConversationsForDoctor(doctorId)

    suspend fun findConversationsForPatient(patientId: String): LoadState<List<MessageThread>> = messageRepository.findConversationsForPatient(patientId)

    suspend fun findConversationsForSecretary(secretaryId: String): LoadState<List<MessageThread>> = messageRepository.findConversationsForSecretary(secretaryId)

    suspend fun findMessages(conversationId: String): LoadState<List<Message>> = messageRepository.findMessages(conversationId)

    suspend fun createMessage(message: Message): LoadState<Message> = messageRepository.createMessage(message)

    suspend fun markAsRead(conversationId: String, userId: String): LoadState<Unit> = messageRepository.markAsRead(conversationId, userId)

    suspend fun createConversation(thread: MessageThread): LoadState<MessageThread> = messageRepository.createConversation(thread)
}