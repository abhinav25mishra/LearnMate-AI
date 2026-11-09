package com.learnmate.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.pytorch.executorch.extension.llm.LlmCallback
import org.pytorch.executorch.extension.llm.LlmModule
import java.io.File

object LocalLlamaEngine : AiMentorEngine {

    private const val MODEL_PATH = "/data/local/tmp/llama/llama3_2_1b_int4.pte"
    private const val TOKENIZER_PATH = "/data/local/tmp/llama/tokenizer.model"

    private var llmModule: LlmModule? = null

    private fun ensureLoaded(): Boolean {
        if (llmModule != null) return true

        val modelFile = File(MODEL_PATH)
        val tokenizerFile = File(TOKENIZER_PATH)

        if (!modelFile.exists() || !tokenizerFile.exists()) {
            PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                modelSizeMb = "Missing (.pte not pushed)",
                inferenceStatus = "Fallback Active (Push .pte to /data/local/tmp/llama/)"
            )
            return false
        }

        val startMs = System.currentTimeMillis()
        llmModule = LlmModule(MODEL_PATH, TOKENIZER_PATH, 0.8f)
        val loadTime = System.currentTimeMillis() - startMs
        val sizeMb = modelFile.length() / (1024 * 1024)

        PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
            modelSizeMb = "$sizeMb MB",
            loadTimeMs = "$loadTime ms",
            inferenceStatus = "ExecuTorch Ready (On-Device)"
        )
        return true
    }

    override suspend fun askMentor(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String = withContext(Dispatchers.IO) {
        if (!ensureLoaded()) {
            return@withContext MockAiMentorEngine.askMentor(question, profile, difficulty, hintOnly)
        }

        val modeInstruction = if (hintOnly) {
            "Give a short Socratic hint only. Do NOT give the full answer."
        } else {
            "Give a clear, step-by-step explanation."
        }

        val prompt = "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\n" +
            "You are LearnMate AI, an offline ${profile.subject} tutor. " +
            "Student level: $difficulty. Language: ${profile.preferredLanguage}. $modeInstruction" +
            "<|eot_id|><|start_header_id|>user<|end_header_id|>\n" +
            "$question<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n"

        val outputBuilder = StringBuilder()
        val startTime = System.currentTimeMillis()
        var firstTokenTime: Long? = null
        var tokenCount = 0

        llmModule?.generate(prompt, 128, object : LlmCallback {
            override fun onResult(result: String) {
                if (firstTokenTime == null) {
                    firstTokenTime = System.currentTimeMillis() - startTime
                }
                tokenCount++
                outputBuilder.append(result)
            }
        })

        val totalSec = (System.currentTimeMillis() - startTime).coerceAtLeast(1) / 1000f
        val tps = String.format("%.1f tok/s", tokenCount / totalSec)
        PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
            ttftMs = "${firstTokenTime ?: "--"} ms",
            tokensPerSec = tps,
            inferenceStatus = "ExecuTorch Active"
        )

        outputBuilder.toString().ifBlank {
            MockAiMentorEngine.askMentor(question, profile, difficulty, hintOnly)
        }
    }
}