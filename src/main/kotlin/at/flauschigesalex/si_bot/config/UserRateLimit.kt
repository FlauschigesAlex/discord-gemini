package at.flauschigesalex.si_bot.config

import at.flauschigesalex.si_bot.utils.extensions.now
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.Member
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@Serializable
class UserRateLimit @ConfigInternal internal constructor() {
    companion object {
        private val rateLimit = mutableListOf<RateLimitEntry>()
            get() {
                field.removeIf { it.timestamp < Instant.now() }
                return field
            }
        
        val Member.isRateLimitedUntil: Instant?
            get() {
                val limits = rateLimit.filter {
                    it.userId == this.idLong
                }
                
                val RateLimit = BotConfig.INSTANCE.RateLimits
                if (RateLimit != null && limits.size < RateLimit.maxRequests.toInt())
                    return null
                
                return limits.minOfOrNull { it.timestamp }
            }
        
        fun Member.addRateLimit() {
            BotConfig.INSTANCE.RateLimits ?: return
            rateLimit.add(RateLimitEntry(this.idLong))
        }
        
    }
    
    val maxRequests: UInt = 10u
    val timeframe: Duration = 5.minutes
}

typealias UserId = Long
data class RateLimitEntry(
    val userId: UserId,
    val timestamp: Instant = Clock.System.now() + BotConfig.INSTANCE.RateLimits!!.timeframe
)