package at.flauschigesalex.si_bot

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.base.file.json.JsonManager
import at.flauschigesalex.lib.base.file.json.deserializeOrThrow
import at.flauschigesalex.lib.base.file.json.readJson
import at.flauschigesalex.si_bot.ai.data.ChannelData
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JDuration

@Serializable
class BotConfig private constructor() {
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
    val messages: MessageConfig = MessageConfig()
    
    val contextSize: UInt = 10u
    val contextMemory: Duration = JDuration.ofMinutes(5).toKotlinDuration()
    
    fun getChannelOverride(channel: GuildMessageChannel): ChannelData =
        channelOverrides.find { it == channel } ?: this.getDefaultChannelData()
    
    private fun getDefaultChannelData(): ChannelData =
        channelOverrides.find { it.channelId == ChannelData.defaultId } ?: ChannelData.default
}

@Serializable
class MessageConfig internal constructor() {
    companion object;
    
    fun randomThinking(): String = thinking.randomOrNull() ?: "Thinking..."
    private val thinking: List<String> = listOf(
        "Working on it...",
        "Just a moment...",
        "Hang on...",
        "Thinking...",
        "Crafting response..."
    )

    fun randomFailure(): String = failure.randomOrNull() ?: "I'm sorry, but I couldn't process your request."
    val failure: List<String> = listOf(
        "I'm sorry, but I couldn't process your request.",
        "Oops, something went wrong. Please try again later.",
        "Sorry, I'm having trouble with that. Can you try again?",
        "I'm sorry, but I wasn't able to provide a response at this time.",
        "I'm sorry, but I wasn't able to process your request at this time."
    )
}