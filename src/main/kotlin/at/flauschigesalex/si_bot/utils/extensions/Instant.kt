package at.flauschigesalex.si_bot.utils.extensions

import kotlin.time.Clock
import kotlin.time.Instant

companion fun Instant.now(): Instant = Clock.System.now()