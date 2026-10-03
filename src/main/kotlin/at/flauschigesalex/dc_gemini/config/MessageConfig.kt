package at.flauschigesalex.dc_gemini.config

import kotlinx.serialization.Serializable

@Serializable
class MessageConfig @ConfigInternal internal constructor() {
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

    fun randomRateLimit(): String = rateLimit.randomOrNull() ?: "You are being rate limited. Try again %duration%."
    val rateLimit: List<String> = listOf(
        "You are being rate limited. Try again %duration%.",
    )
}