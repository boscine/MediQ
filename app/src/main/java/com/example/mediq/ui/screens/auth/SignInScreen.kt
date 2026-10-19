package com.example.mediq.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mediq.ui.navigation.Screen
import com.example.mediq.ui.theme.MediQBackground
import com.example.mediq.ui.theme.MediQSize
import com.example.mediq.ui.theme.MediQSpacing
import com.example.mediq.ui.theme.MediQTextPrimary
import com.example.mediq.ui.theme.MediQTextSecondary

/**
 * Sign-in.
 *
 * Job: get an existing patient in. Primary action: sign in.
 *
 * [state] defaults to the current mock source, [AuthSamples]. Replacing it with
 * a repository changes this one call site, not the layout.
 */
@Composable
fun SignInScreen(
    navController: NavController,
    state: SignInState = AuthSamples.signInIdle
) {
    SignInContent(
        state = state,
        actions = SignInActions(
            onSubmit = { _, _ ->
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.SignIn.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onRegister = { navController.navigate(Screen.RegisterDetails.route) }
        )
    )
}

/**
 * The sign-in form.
 *
 * The two values and their "you left this blank" errors are view state the
 * patient owns, so they stay put while [state] moves through submitting and
 * rejected — a failed sign-in never clears what was typed.
 *
 * [initialErrors] is the seam a restored form (or a preview of the invalid
 * state) enters through; nothing else passes anything but the clean value.
 */
@Composable
fun SignInContent(
    state: SignInState,
    actions: SignInActions,
    initialErrors: SignInErrors = SignInErrors(null, null),
    modifier: Modifier = Modifier
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errors by remember { mutableStateOf(initialErrors) }

    val submitting = state == SignInState.SigningIn

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MediQBackground)
            .verticalScroll(rememberScrollState())
    ) {
        AuthHeroBand()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MediQSpacing.gutter)
        ) {
            // Clear the mark that overhangs the photo band.
            Spacer(Modifier.height(MediQSize.authLogo / 2))

            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.titleLarge,
                color = MediQTextPrimary
            )

            Spacer(Modifier.height(MediQSpacing.xs))

            Text(
                text = "Sign in to book consultations and manage your schedule.",
                style = MaterialTheme.typography.bodyMedium,
                color = MediQTextSecondary
            )

            Spacer(Modifier.height(MediQSpacing.xxl))

            AuthField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Your username",
                error = errors.username,
                enabled = !submitting
            )

            Spacer(Modifier.height(MediQSpacing.lg))

            AuthPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Your password",
                error = errors.password,
                enabled = !submitting
            )

            ForgotPasswordRow(
                onForgotPassword = actions.onForgotPassword,
                enabled = !submitting
            )

            SignInNotice(state = state)

            AuthPrimaryButton(
                label = if (submitting) "Signing in..." else "Sign in",
                enabled = !submitting,
                onClick = {
                    val checked = AuthValidation.validateSignIn(username, password)
                    errors = checked
                    if (checked.isClean) actions.onSubmit(username, password)
                }
            )

            Spacer(Modifier.height(MediQSpacing.md))

            AuthSecondaryButton(
                label = "Create Account / Register",
                enabled = !submitting,
                onClick = actions.onRegister
            )

            Spacer(Modifier.height(MediQSpacing.xxl))
        }
    }
}
