package com.example.mediq.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mediq.ui.navigation.Screen
import com.example.mediq.ui.theme.MediQBackground
import com.example.mediq.ui.theme.MediQElevation
import com.example.mediq.ui.theme.MediQGreen
import com.example.mediq.ui.theme.MediQNeutralContainer
import com.example.mediq.ui.theme.MediQOnBrand
import com.example.mediq.ui.theme.MediQRadius
import com.example.mediq.ui.theme.MediQScaffoldBackground
import com.example.mediq.ui.theme.MediQSize
import com.example.mediq.ui.theme.MediQSpacing
import com.example.mediq.ui.theme.MediQTextPrimary
import com.example.mediq.ui.theme.MediQTextSecondary

/**
 * Home.
 *
 * Job: orient the patient — show the next appointment, then get them booking.
 * Primary action: "Book a consultation".
 *
 * [state] defaults to the current mock source, [HomeSamples]. Swapping that for
 * a real data source changes this one call site, not the layout.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    state: HomeUiState = HomeSamples.content
) {
    HomeContent(
        state = state,
        actions = HomeActions(
            onNotificationsClick = {
                navController.navigate(Screen.Notifications.route)
            },
            onBrowseDoctors = {
                navController.navigate(Screen.Doctors.route)
            },
            onDoctorClick = { doctorId ->
                navController.navigate(Screen.DoctorDetails.createRoute(doctorId))
            },
            onManageAppointment = { appointmentId ->
                navController.navigate(Screen.AppointmentDetails.createRoute(appointmentId))
            }
        )
    )
}

/** State switch. Everything below this point is navigation-free and previewable. */
@Composable
fun HomeContent(
    state: HomeUiState,
    actions: HomeActions
) {
    when (state) {
        is HomeUiState.Content -> HomeLoaded(state = state, actions = actions)
        HomeUiState.Loading -> HomeLoadingState()
        is HomeUiState.Error -> HomeErrorState(
            message = state.message,
            onRetry = actions.onRetry
        )
    }
}

/** Height of the green band behind the header. */
private val HeaderHeight = 132.dp

/** How far the next-consultation card overlaps the green band. */
private val CardTopOffset = 90.dp

@Composable
private fun HomeLoaded(
    state: HomeUiState.Content,
    actions: HomeActions
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MediQBackground),
        contentPadding = PaddingValues(bottom = MediQSpacing.xxl),
        verticalArrangement = Arrangement.spacedBy(MediQSpacing.lg)
    ) {

        item {
            Box(modifier = Modifier.fillMaxWidth()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HeaderHeight)
                        .background(MediQGreen)
                        .align(Alignment.TopCenter)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = CardTopOffset,
                            start = MediQSpacing.gutter,
                            end = MediQSpacing.gutter
                        )
                ) {
                    val appointment = state.nextAppointment
                    if (appointment == null) {
                        NoUpcomingAppointmentCard()
                    } else {
                        NextConsultationCard(
                            appointment = appointment,
                            onManageAppointment = actions.onManageAppointment
                        )
                    }
                }

                HomeHeader(
                    patient = state.patient,
                    unreadNotifications = state.unreadNotifications,
                    onNotificationsClick = actions.onNotificationsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MediQSpacing.gutter)
            ) {
                BookConsultationButton(onClick = actions.onBrowseDoctors)
            }
        }

        item {
            SpecialtyChips(
                specialties = state.specialties,
                onSpecialtyClick = { actions.onBrowseDoctors() }
            )
        }

        item {
            OpenSlotsCard(
                doctors = state.openSlots,
                onDoctorClick = actions.onDoctorClick,
                modifier = Modifier.padding(horizontal = MediQSpacing.gutter)
            )
        }
    }
}

/**
 * The single primary action on the screen.
 *
 * Labelled with text and an icon, and sized at the 48dp minimum touch target.
 */
@Composable
private fun BookConsultationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(MediQSize.minTouchTarget),
        shape = RoundedCornerShape(MediQRadius.md)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = MediQOnBrand,
            modifier = Modifier.size(MediQSize.iconLarge)
        )
        Spacer(Modifier.size(MediQSpacing.sm))
        Text(
            text = "Book a consultation",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/** Skeleton that mirrors the loaded layout, so nothing jumps when data arrives. */
@Composable
private fun HomeLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MediQScaffoldBackground)
            .padding(horizontal = MediQSpacing.gutter)
            .semantics { contentDescription = "Loading your home screen" }
    ) {
        Spacer(Modifier.height(MediQSpacing.xxl))

        SkeletonBar(widthFraction = 0.45f, height = 20.dp)

        Spacer(Modifier.height(MediQSpacing.lg))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MediQBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = MediQElevation.none)
        ) {
            Column(modifier = Modifier.padding(MediQSpacing.lg)) {
                SkeletonBar(widthFraction = 0.55f, height = 14.dp)
                Spacer(Modifier.height(MediQSpacing.md))
                SkeletonBar(widthFraction = 0.35f, height = 28.dp)
                Spacer(Modifier.height(MediQSpacing.sm))
                SkeletonBar(widthFraction = 0.7f, height = 14.dp)
                Spacer(Modifier.height(MediQSpacing.lg))
                SkeletonBar(widthFraction = 0.5f, height = 14.dp)
            }
        }

        Spacer(Modifier.height(MediQSpacing.lg))

        SkeletonBar(widthFraction = 0.6f, height = MediQSize.minTouchTarget)
    }
}

@Composable
private fun SkeletonBar(widthFraction: Float, height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(MaterialTheme.shapes.small)
            .background(MediQNeutralContainer)
            .clearAndSetSemantics { }
    )
}

/** Recoverable failure: says what happened and offers the retry. */
@Composable
private fun HomeErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MediQScaffoldBackground)
            .padding(MediQSpacing.gutter),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
            tint = MediQTextSecondary,
            modifier = Modifier.size(MediQSize.control)
        )

        Spacer(Modifier.height(MediQSpacing.lg))

        Text(
            text = "We couldn't load your home screen",
            style = MaterialTheme.typography.titleSmall,
            color = MediQTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(MediQSpacing.sm))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MediQTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(MediQSpacing.xl))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(MediQRadius.md),
            modifier = Modifier.height(MediQSize.minTouchTarget)
        ) {
            Text(
                text = "Try again",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
