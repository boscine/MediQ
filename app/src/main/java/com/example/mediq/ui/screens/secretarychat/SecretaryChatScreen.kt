package com.example.mediq.ui.screens.secretarychat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mediq.ui.navigation.Screen
import com.example.mediq.ui.theme.MediQScaffoldBackground

/**
 * Secretary chat.
 *
 * Job: resolve a question. Primary action: send.
 *
 * [state] defaults to the mock source. Replacing it with a repository changes
 * this one call site.
 */
@Composable
fun SecretaryChatScreen(
    navController: NavController,
    conversationId: String = "maria_secretary",
    state: ChatUiState = SecretaryChatSamples.maria
) {
    SecretaryChatContent(
        state = state,
        actions = ChatActions(
            onBack = { navController.navigate(Screen.Messages.route) { launchSingleTop = true } }
        )
    )
}

/** State switch. Everything below is navigation-free and previewable. */
@Composable
fun SecretaryChatContent(
    state: ChatUiState,
    actions: ChatActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MediQScaffoldBackground)
    ) {
        when (state) {
            ChatUiState.Loading -> ChatLoadingState()
            is ChatUiState.Error -> ChatErrorState(message = state.message, onRetry = actions.onBack)
            is ChatUiState.Content -> {
                ChatHeader(secretaryName = state.secretaryName, onBack = actions.onBack)
                ChatNotice()
                ChatMessageList(messages = state.messages)
                ChatInput(text = "", onTextChange = {}, onSend = { actions.onSend("") })
            }
        }
    }
}