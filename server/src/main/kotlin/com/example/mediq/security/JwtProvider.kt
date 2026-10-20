package com.example.mediq.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import com.example.mediq.domain.model.UserProfile
import com.example.mediq.util.plusDays
import com.example.mediq.util.plusMinutes
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneOffset
import java.util.Date

class JwtProvider(
    private val secret: String,
    private val issuer: String = "mediq",
) {
    internal val algorithm: Algorithm = Algorithm.HMAC256(secret)

    /** Verifier used by the Ktor JWT authentication plugin. */
    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    fun createAccessToken(profile: UserProfile): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val expiresAt = now.plusMinutes(15)
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(profile.id)
            .withClaim("username", profile.username)
            .withClaim("role", profile.role.serialName)
            .withClaim("fullName", profile.fullName)
            .withIssuedAt(Date.from(now.toJavaInstant()))
            .withExpiresAt(Date.from(expiresAt.toJavaInstant()))
            .sign(algorithm)
    }

    fun createRefreshToken(userId: String): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val expiresAt = now.plusDays(30)
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId)
            .withClaim("type", "refresh")
            .withIssuedAt(Date.from(now.toJavaInstant()))
            .withExpiresAt(Date.from(expiresAt.toJavaInstant()))
            .sign(algorithm)
    }

    fun verifyAccessToken(token: String): JwtClaims? {
        return try {
            val decoded = verifier.verify(token)
            JwtClaims(
                userId = decoded.subject!!,
                username = decoded.getClaim("username").asString(),
                role = decoded.getClaim("role").asString(),
                fullName = decoded.getClaim("fullName").asString(),
                expiresAt = decoded.expiresAt?.let { Instant.fromEpochMilliseconds(it.time).toLocalDateTime(TimeZone.UTC) },
            )
        } catch (e: JWTVerificationException) {
            null
        }
    }

    fun verifyRefreshToken(token: String): String? {
        return try {
            val decoded = verifier.verify(token)
            if (decoded.getClaim("type").asString() == "refresh") {
                decoded.subject
            } else {
                null
            }
        } catch (e: JWTVerificationException) {
            null
        }
    }

    private fun LocalDateTime.toJavaInstant(): java.time.Instant =
        toJavaLocalDateTime().toInstant(ZoneOffset.UTC)
}

data class JwtClaims(
    val userId: String,
    val username: String,
    val role: String,
    val fullName: String,
    val expiresAt: LocalDateTime? = null,
)
