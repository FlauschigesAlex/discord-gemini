package at.flauschigesalex.si_bot.listener

import at.flauschigesalex.lib.discord.listener.DiscordListener
import at.flauschigesalex.si_bot.BotConfig
import at.flauschigesalex.si_bot.JDA
import at.flauschigesalex.si_bot.ai.AIPrompt
import at.flauschigesalex.si_bot.ai.Koog
import at.flauschigesalex.si_bot.ai.data.ResponseAction
import at.flauschigesalex.si_bot.utils.serializer.Serialized
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.Instant
import kotlin.time.toJavaDuration

@Suppress("unused")
private class MessageListener : DiscordListener() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val mention = JDA.selfUser.asMention
        val message = event.message
        val content = message.contentRaw
        
        val author = event.member ?: return
        val user = author.user
        
        val channel = message.channel as? GuildMessageChannel ?: return
        
        val BotConfig = BotConfig.INSTANCE
        val BotMessages = BotConfig.messages

        val action = BotConfig.getChannelOverride(channel).action
        val relation = message.messageReference?.message

        val shouldRespond: Boolean = when {
            user.isBot || user.isSystem -> return
            
            action == ResponseAction.NEVER_REPLY -> return
            relation != null && relation.author == JDA.selfUser -> {
                action == ResponseAction.ALWAYS_REPLY
            }
            
            else -> content.contains(mention)
        }
        
        if (shouldRespond.not()) return
        
        message.reply(BotMessages.randomThinking()).queue { message ->
            val userPrompt = content.replace(mention, "").trim()
            
            val relations = mutableListOf<Message>()
            
            var relation = message.messageReference?.message
            while (relation != null) {
                relations + relation
                if (relations.size > BotConfig.contextSize.toInt()) break
                relation = relation.messageReference?.message
            }
            
            channel.getHistoryBefore(message, BotConfig.contextSize.toInt()).queue { history ->
                val messages = history.retrievedHistory
                    .filter { it.timeCreated.toInstant().isAfter(Instant.now().minus(BotConfig.contextMemory.toJavaDuration())) }
                    .mapNotNull { it.Serialized }
                    .takeIf { it.isNotEmpty() }

                val effectivePrompt = userPrompt.takeIf { it.isNotBlank() }
                    ?: relations.firstOrNull()?.contentRaw?.takeIf { it.isNotBlank() }
                    ?: "Reply to the provided messages."
                
                val prompt = AIPrompt(messages, effectivePrompt).relate(relations.map { it.Serialized })
                Koog.invokeAgentConsuming(prompt) {
                    val reply = it.getOrElse { err -> "${BotMessages.randomFailure()}\n${err.message ?: err.javaClass.name}" }.take(2000)
                    message.editMessage(reply).queue()
                }
            }
        }
    }
}