@file:OptIn(ConfigInternal::class)

package at.flauschigesalex.si_bot.config

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.base.file.json.JsonManager
import at.flauschigesalex.lib.base.file.json.deserializeOrThrow
import at.flauschigesalex.lib.base.file.json.readJson
import at.flauschigesalex.si_bot.ai.data.ChannelData
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
@Suppress("RedundantNullableReturnType")
class BotConfig @ConfigInternal private constructor() {
    companion object {
        private var instance: BotConfig? = null
        
        val INSTANCE: BotConfig
            get() = instance ?: run {
                val file = FileManager("config.json")
                if (file.exists.not()) {
                    file.createFile()
                }

                val json = file.readJson() ?: JsonManager()
                val instance = json.deserializeOrThrow<BotConfig>()

                this.instance = instance
                return@run instance
            }
        
    }
    
    private val channelOverrides: MutableSet<ChannelData> = mutableSetOf()
    
    val Messages: MessageConfig = MessageConfig()
    val RateLimits: UserRateLimit? = UserRateLimit()
    
    val contextSize: UInt = 10u
    val contextMemory: Duration = 5.minutes
    
    fun getChannelOverride(channel: GuildMessageChannel): ChannelData =
        channelOverrides.find { it == channel } ?: this.getDefaultChannelData()
    
    private fun getDefaultChannelData(): ChannelData =
        channelOverrides.find { it.channelId == ChannelData.defaultId } ?: ChannelData.default
}

@RequiresOptIn("", RequiresOptIn.Level.ERROR)
annotation class ConfigInternal