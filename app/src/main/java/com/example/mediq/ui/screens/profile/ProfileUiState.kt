package com.example.mediq.ui.screens.profile

/*
 * What the profile and edit-profile screens can be doing.
 */

data class ProfileData(
    val fullName: String,
    val email: String,
    val initials: String,
    val phoneNumber: String
)

val MockProfile = ProfileData(
    fullName = "Arweyne Zoe Salcedo",
    email = "zoe.salcedo@email.com",
    initials = "AZ",
    phoneNumber = "0917 442 8810"
)

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    data class Content(val profile: ProfileData) : ProfileUiState
}

data class ProfileActions(
    val onEdit: () -> Unit = {},
    val onSignOut: () -> Unit = {}
)

/** Edit profile is simpler: idle, dirty, saving, saved, error. */
sealed interface EditProfileUiState {
    data object Idle : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
    data object Saving : EditProfileUiState
    data object Saved : EditProfileUiState
}

data class EditProfileActions(
    val onBack: () -> Unit = {},
    val onSave: (String, String) -> Unit = { _, _ -> }
)