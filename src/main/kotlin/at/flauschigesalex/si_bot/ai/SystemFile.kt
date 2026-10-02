package at.flauschigesalex.si_bot.ai

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.base.file.ResourceManager
import at.flauschigesalex.si_bot.JDA
import net.dv8tion.jda.api.JDA

object SystemFile {
    lateinit var systemPrompt: String
        private set

    private fun readResource(): String? = ResourceManager("system-prompt.md")?.readString()
    private fun readFile(): String? = FileManager("system-prompt.md").apply { 
        this.createFile()
    }.readString()
    
    fun reloadSystemPrompt(JDA: JDA) {
        systemPrompt = ""
        
        readResource()?.takeIf { it.isNotBlank() }?.let {
            if (systemPrompt.isNotBlank())
                systemPrompt += "\n\n"
            
            systemPrompt += it.format(JDA)
        }
        readFile()?.takeIf { it.isNotBlank() }?.let {
            if (systemPrompt.isNotBlank())
                systemPrompt += "\n\n"
            
            systemPrompt += it.format(JDA)
        }
    }
    
    private fun String.format(JDA: JDA) = this.replace("%bot-name%", JDA.selfUser.name)
    
    init {
        this.reloadSystemPrompt(JDA)
    }
}