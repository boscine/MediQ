package com.example.mediq.ui.screens.home

/*
 * Home screen state and the callbacks it raises.
 *
 * Presentation never receives a NavController: components take plain lambdas so
 * every state is renderable in a @Preview and assertable in a UI test.
 */

/** Patient identity shown in the home header. */
data class PatientSummary(
    val firstName: String,
    val fullName: String
) {
    /**
     * Up to two initials, e.g. "Arweyne Zabala" -> "AZ".
     * Derived rather than hardcoded, so the avatar follows the name.
     */
    val initials: String = fullName
        .split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
}

/**
 * Status of a booked appointment.
 *
 * [label] is required: status is never conveyed by colour alone.
 */
enum class AppointmentStatus(val label: String) {
    Confirmed("Confirmed"),
    Pending("Awaiting confirmation"),
    Cancelled("Cancelled")
}

/** The single appointment surfaced on the home screen. */
data class NextAppointment(
    val id: String,
    val time: String,
    val date: String,
    val doctorName: String,
    val specialty: String,
    val location: String,
    val arrivalNote: String,
    val status: AppointmentStatus
)

/** A doctor ranked by open slots this week. */
data class DoctorAvailability(
    val doctorId: String,
    val name: String,
    val specialty: String,
    val location: String,
    val openSlots: Int
)

/** Everything the home screen renders. */
sealed interface HomeUiState {

    /** Skeleton that matches the loaded layout. */
    data object Loading : HomeUiState

    /**
     * Home loaded.
     *
     * [nextAppointment] is null when nothing is booked — that is a normal state,
     * not an error, and renders the empty card.
     */
    data class Content(
        val patient: PatientSummary,
        val nextAppointment: NextAppointment?,
        val specialties: List<String>,
        val openSlots: List<DoctorAvailability>,
        val unreadNotifications: Int = 0
    ) : HomeUiState

    /** Home could not load. */
    data class Error(val message: String) : HomeUiState
}

/** Callbacks the home screen raises. All optional so previews stay terse. */
data class HomeActions(
    val onNotificationsClick: () -> Unit = {},
    val onBrowseDoctors: () -> Unit = {},
    val onDoctorClick: (String) -> Unit = {},
    val onManageAppointment: (String) -> Unit = {},
    val onRetry: () -> Unit = {}
)
