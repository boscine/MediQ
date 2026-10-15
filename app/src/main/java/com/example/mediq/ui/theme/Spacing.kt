package com.example.mediq.ui.theme

import androidx.compose.ui.unit.dp

/*
 * MediQ spacing, radius, elevation and sizing tokens.
 *
 * Rules:
 *  - No arbitrary dp values in screens. If a value is not on this scale, question it.
 *  - Screen horizontal padding is always [MediQSpacing.gutter].
 */

/** The 4dp spacing scale. */
object MediQSpacing {

    /** 2dp — hairline gaps inside a badge. */
    val xxs = 2.dp

    /** 4dp — icon-to-label tightening. */
    val xs = 4.dp

    /** 8dp — related elements in the same group. */
    val sm = 8.dp

    /** 12dp — control padding, list row gaps. */
    val md = 12.dp

    /** 16dp — card padding, section gaps. */
    val lg = 16.dp

    /** 20dp — screen gutter. Every screen's horizontal padding. */
    val gutter = 20.dp

    /** 20dp — alias for [gutter] when read as a generic step. */
    val xl = 20.dp

    /** 24dp — screen bottom padding below scrollable content. */
    val xxl = 24.dp

    /** 32dp — hero and empty-state breathing room. */
    val xxxl = 32.dp
}

/** Corner radii. Three steps plus a pill; do not introduce a fourth. */
object MediQRadius {

    /** 8dp — chips, small controls, inner tiles. */
    val sm = 8.dp

    /** 12dp — buttons and text fields. */
    val md = 12.dp

    /** 16dp — cards and sheets. */
    val lg = 16.dp

    /** Full pill — status badges and filter chips. */
    val pill = 999.dp
}

/** Elevation. The design is flat and bordered; shadows stay near zero. */
object MediQElevation {

    /** 0dp — flat surfaces and bordered cards. */
    val none = 0.dp

    /** 2dp — cards that must lift off the scaffold background. Was 5dp. */
    val card = 2.dp
}

/** Fixed component sizes that are not spacing. */
object MediQSize {

    /** 48dp — minimum touch target for any interactive element (WCAG 2.1 AA). */
    val minTouchTarget = 48.dp

    /** 40dp — visual size of an icon button or avatar. Pad up to [minTouchTarget]. */
    val control = 40.dp

    /** 48dp — initials avatar in a list row. */
    val avatar = 48.dp

    /** 18dp — standard inline icon. */
    val icon = 18.dp

    /** 20dp — icon inside a primary button. */
    val iconLarge = 20.dp

    /** 2dp — avatar and badge border width. */
    val hairline = 1.dp
}
