package at.flauschigesalex.dc_gemini.utils.serializer

import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.Message

@Serializable
data class SerializedMessage(
    val author: String,
    val message: String,
    val timestamp: Long,
    val attachments: List<String>,
)

val Message.Serialized: SerializedMessage
    get() = SerializedMessage(
        author = author.name,
        message = contentRaw,
        timestamp = timeCreated.toEpochSecond(),
        attachments = attachments.mapNotNull { it.url }
    )