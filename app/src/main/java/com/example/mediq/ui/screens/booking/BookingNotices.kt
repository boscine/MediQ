package com.example.mediq.ui.screens.booking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mediq.ui.theme.MediQError
import com.example.mediq.ui.theme.MediQErrorContainer
import com.example.mediq.ui.theme.MediQSize
import com.example.mediq.ui.theme.MediQSpacing
import com.example.mediq.ui.theme.MediQTextPrimary
import com.example.mediq.ui.theme.MediQWarning
import com.example.mediq.ui.theme.MediQWarningContainer

/**
 * What went wrong with the submission, if anything.
 *
 * A slot that went to someone else gets the way out in the notice itself —
 * there is nothing to retry, so the notice says so instead of leaving a dead
 * error up.
 */
@Composable
internal fun SubmissionNotice(
    submission: BookingSubmission,
    actions: BookingActions,
    modifier: Modifier = Modifier
) {
    when (submission) {
        is BookingSubmission.SlotTaken -> Notice(
            icon = Icons.Outlined.CalendarMonth,
            message = submission.message,
            container = MediQWarningContainer,
            content = MediQWarning,
            actionLabel = "Choose another slot",
            onAction = actions.onChooseAnotherSlot,
            modifier = modifier
        )

        is BookingSubmission.Error -> Notice(
            icon = Icons.Outlined.CloudOff,
            message = submission.message,
            container = MediQErrorContainer,
            content = MediQError,
            actionLabel = null,
            onAction = null,
            modifier = modifier
        )

        BookingSubmission.Idle, BookingSubmission.Submitting -> Unit
    }
}

@Composable
private fun Notice(
    icon: ImageVector,
    message: String,
    container: Color,
    content: Color,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = container
    ) {
        Row(
            modifier = Modifier.padding(MediQSpacing.md),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier
                    .size(MediQSize.icon)
                    .padding(top = MediQSpacing.xxs)
            )

            Spacer(Modifier.width(MediQSpacing.sm))

            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediQTextPrimary
                )

                if (actionLabel != null && onAction != null) {
                    TextButton(
                        onClick = onAction,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.heightIn(min = MediQSize.minTouchTarget)
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = content
                        )
                    }
                }
            }
        }
    }
}
