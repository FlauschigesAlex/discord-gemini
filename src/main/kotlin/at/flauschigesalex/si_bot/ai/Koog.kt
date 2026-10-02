package at.flauschigesalex.si_bot.ai

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.GraphAIAgent
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import at.flauschigesalex.lib.base.file.env.Environment
import at.flauschigesalex.si_bot.JDA
import at.flauschigesalex.si_bot.Json
import at.flauschigesalex.si_bot.utils.serializer.SerializedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

object Koog {
    private lateinit var agents: Set<GraphAIAgent<String, String>>
    
    fun reloadAgents() {
        agents = setOfNotNull(
            Environment["GEMINI_API_KEY"]?.let {
                AIAgent(
                    promptExecutor = MultiLLMPromptExecutor(GoogleLLMClient(it)),
                    llmModel = GoogleModels.Gemini3_5FlashLite,
                    systemPrompt = SystemFile.systemPrompt
                )
            },
            Environment["GEMINI_API_KEY"]?.let {
                AIAgent(
                    promptExecutor = MultiLLMPromptExecutor(GoogleLLMClient(it)),
                    llmModel = GoogleModels.Gemini3_1FlashLite,
                    systemPrompt = SystemFile.systemPrompt
                )
            }
        )
    }
    
    init {
        SystemFile.reloadSystemPrompt(JDA)
        reloadAgents()
    }
    
    suspend fun invokeAgent(prompt: AIPrompt): Result<String> = runCatching {
        val agents = agents.toMutableSet()
        
        while (agents.isNotEmpty()) {
            runCatching ignore@ {
                val agent = agents.first()
                val serialized = Json.encodeToString(prompt)

                val result = agent.run(serialized)
                agents.remove(agent)
                
                return@runCatching result
            }
        }
        
        throw IllegalStateException("No more agents available")
    }
    
    private val scope = CoroutineScope(Dispatchers.Default)
    fun invokeAgentConsuming(prompt: AIPrompt, consumer: (Result<String>) -> Unit) {
        scope.launch {
            consumer(invokeAgent(prompt))
        }
    }
}

@Serializable
data class AIPrompt(
    val context: List<SerializedMessage>?,
    val prompt: String,
) {
    companion object;
    
    var relation: List<SerializedMessage>? = null
        private set
    
    fun relate(relation: SerializedMessage?): AIPrompt = this.relate(listOfNotNull(relation))
    fun relate(relation: List<SerializedMessage>?): AIPrompt = apply {
        this.relation = relation?.takeIf { it.isNotEmpty() }
    }
}
