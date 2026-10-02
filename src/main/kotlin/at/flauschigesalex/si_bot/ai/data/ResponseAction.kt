package at.flauschigesalex.si_bot.ai.data

import kotlinx.serialization.Serializable

@Serializable
@Suppress("unused")
enum class ResponseAction {
    NEVER_REPLY,
    ONLY_MENTION,
    ALWAYS_REPLY,
    ;
    
    companion object;
}