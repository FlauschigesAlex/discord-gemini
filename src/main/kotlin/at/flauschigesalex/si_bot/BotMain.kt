package at.flauschigesalex.si_bot

import at.flauschigesalex.lib.base.file.env.Environment
import at.flauschigesalex.lib.base.file.json.JsonManager
import at.flauschigesalex.lib.discord.FlauschigeLibraryDiscord
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

object Bot {
    @JvmStatic fun main(args: Array<String>) = Unit
    
    val JDA: JDA = JDABuilder.createDefault(Environment["DISCORD_TOKEN"], GatewayIntent.entries).build()
        .awaitReady()
    
    init {
        FlauschigeLibraryDiscord.init(JDA, Bot::class.java)
        JsonManager.json = Json
    }
}

val JDA: JDA
    get() = Bot.JDA

val Json = Json { 
    ignoreUnknownKeys = true
    encodeDefaults = true
    prettyPrint = true
    
    serializersModule = SerializersModule { 
    }
}