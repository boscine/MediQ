package com.example.mediq.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.mediq.ui.components.MediQBackHeader
import com.example.mediq.ui.navigation.Screen
import com.example.mediq.ui.theme.MediQBackground
import com.example.mediq.ui.theme.MediQSpacing
import com.example.mediq.ui.theme.MediQTextPrimary
import com.example.mediq.ui.theme.MediQTextSecondary
import kotlinx.coroutines.delay

/** The saved-state key the details step leaves the number under. */
internal const val OTP_PHONE_KEY = "register_phone_number"

/** How often the resend countdown ticks. */
private const val OTP_TICK_MILLIS = 1_000L

/**
 * Registration step 2: prove the number belongs to the patient.
 *
 * Job: check the one-time code. Primary action: verify.
 */
@Composable
fun RegisterOtpScreen(
    navController: NavController,
    state: OtpState = AuthSamples.otpSent
) {
    var secondsLeft by rememberSaveable {
        mutableIntStateOf(AuthValidation.OTP_RESEND_SECONDS)
    }

    /*
     * One ticker for as long as the screen is open. It only writes state while
     * a countdown is running, and a resend just puts the count back up.
     */
    LaunchedEffect(Unit) {
        while (true) {
            delay(OTP_TICK_MILLIS)
            if (secondsLeft > 0) secondsLeft--
        }
    }

    val phoneNumber = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>(OTP_PHONE_KEY)
        ?: AuthSamples.PHONE_NUMBER

    RegisterOtpContent(
        state = state,
        phoneNumber = phoneNumber,
        resendSecondsLeft = secondsLeft,
        actions = RegisterActions(
            onBack = { navController.popBackStack() },
            onOtpResend = { secondsLeft = AuthValidation.OTP_RESEND_SECONDS },
            onOtpVerify = {
                navController.navigate(Screen.RegisterCredentials.route)
            }
        )
    )
}

/**
 * The code form.
 *
 * [resendSecondsLeft] is handed in rather than counted here, so the countdown
 * lives with the state owner and every state stays renderable from a preview.
 * [initialCodeError] is the same restore seam the other auth forms have.
 */
@Composable
fun RegisterOtpContent(
    state: OtpState,
    phoneNumber: String,
    resendSecondsLeft: Int,
    actions: RegisterActions,
    initialCodeError: String? = null,
    modifier: Modifier = Modifier
) {
    var code by rememberSaveable { mutableStateOf("") }
    var codeError by remember { mutableStateOf(initialCodeError) }

    val verifying = state == OtpState.Verifying
    val resendLocked = resendSecondsLeft > 0 && state != OtpState.Expired

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MediQBackground)
    ) {
        MediQBackHeader(title = REGISTRATION_TITLE, onBack = actions.onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MediQSpacing.gutter)
        ) {
            Spacer(Modifier.height(MediQSpacing.xl))

            RegistrationProgress(currentStep = 2)

            Spacer(Modifier.height(MediQSpacing.xl))

            OtpBadge()

            Spacer(Modifier.height(MediQSpacing.lg))

            Text(
                text = "Verify your number",
                style = MaterialTheme.typography.titleLarge,
                color = MediQTextPrimary
            )

            Spacer(Modifier.height(MediQSpacing.xs))

            Text(
                text = "We sent a ${AuthValidation.OTP_LENGTH}-digit code to $phoneNumber.",
                style = MaterialTheme.typography.bodyMedium,
                color = MediQTextSecondary
            )

            OtpNotice(state = state)

            Spacer(Modifier.height(MediQSpacing.xxl))

            AuthField(
                value = code,
                onValueChange = { typed ->
                    code = typed.filter { it.isDigit() }.take(AuthValidation.OTP_LENGTH)
                    codeError = null
                },
                label = "One-time code",
                placeholder = "123456",
                error = codeError,
                enabled = !verifying,
                keyboardType = KeyboardType.Number
            )

            ResendControl(
                secondsLeft = resendSecondsLeft,
                locked = resendLocked,
                enabled = !verifying,
                onResend = actions.onOtpResend
            )

            Spacer(Modifier.height(MediQSpacing.xxl))

            AuthPrimaryButton(
                label = if (verifying) "Verifying..." else "Verify",
                enabled = !verifying,
                onClick = {
                    val checked = AuthValidation.validateOtp(code)
                    codeError = checked
                    if (checked == null) actions.onOtpVerify(code)
                }
            )

            Spacer(Modifier.height(MediQSpacing.xxl))
        }
    }
}
