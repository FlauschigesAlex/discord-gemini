package at.flauschigesalex.si_bot.ai.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel

@Serializable
@ConsistentCopyVisibility
data class ChannelData private constructor(
    @SerialName("_id") val channelId: String,
    val action: ResponseAction,
) {
    companion object {
        operator fun invoke(
            channel: GuildMessageChannel,
            action: ResponseAction
        ) = ChannelData(channel.id, action)
        
        internal const val defaultId = "_default"
        val default = ChannelData(defaultId, ResponseAction.ALWAYS_REPLY)
        
    }

    override fun equals(other: Any?): Boolean = super.equals(other) || other is GuildMessageChannel && channelId == other.id
    override fun hashCode(): Int = channelId.hashCode()
}