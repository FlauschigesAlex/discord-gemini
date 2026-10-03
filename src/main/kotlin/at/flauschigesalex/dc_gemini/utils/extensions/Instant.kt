package at.flauschigesalex.dc_gemini.utils.extensions

import kotlin.time.Clock
import kotlin.time.Instant

companion fun Instant.now(): Instant = Clock.System.now()