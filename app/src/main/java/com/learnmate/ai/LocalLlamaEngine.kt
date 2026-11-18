package com.learnmate.ai

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.pytorch.executorch.extension.llm.LlmCallback
import org.pytorch.executorch.extension.llm.LlmModule

object LocalLlamaEngine : AiMentorEngine {

    private var llmModule: LlmModule? = null
    private var loadedModelPath: String? = null

    private fun resolveModelFiles(): Pair<File?, File?> {
        val pteCandidates = listOf(
            File("/data/local/tmp/llama/llama3_2_1b_int4.pte"),
            File("/sdcard/Android/data/com.learnmate.ai/files/llama/llama3_2_1b_int4.pte")
        )
        val tokCandidates = listOf(
            File("/data/local/tmp/llama/tokenizer.model"),
            File("/sdcard/Android/data/com.learnmate.ai/files/llama/tokenizer.model")
        )
        val pte = pteCandidates.firstOrNull { it.exists() && it.length() > 10_000_000L }
        val tok = tokCandidates.firstOrNull { it.exists() && it.length() > 100_000L }
        return Pair(pte, tok)
    }

    private fun buildLlama3Prompt(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String {
        val styleInstruction = if (hintOnly) {
            "Give a concise Socratic hint and explain the prerequisite concept without giving away the full answer immediately."
        } else {
            "Provide a clear, complete step-by-step explanation with a practical example."
        }
        val systemPrompt = "You are LearnMate AI, an offline personalized tutor for ${profile.subject} " +
            "(Student level: $difficulty, Preferred language: ${profile.preferredLanguage}, " +
            "Interests: ${profile.interests}). $styleInstruction"

        return "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\n\n" +
            "$systemPrompt<|eot_id|><|start_header_id|>user<|end_header_id|>\n\n" +
            "$question<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n"
    }

    override suspend fun askMentor(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String = withContext(Dispatchers.IO) {
        val (pteFile, tokFile) = resolveModelFiles()

        if (pteFile == null || tokFile == null) {
            PerformanceTracker.latestMetrics = RuntimeMetrics(
                modelSizeMb = "0 MB",
                loadTimeMs = "0 ms",
                ttftMs = "N/A",
                tokensPerSec = "0.0",
                inferenceStatus = "Model Files Missing (/data/local/tmp/llama/)"
            )
            return@withContext "Model files not found on device. Push llama3_2_1b_int4.pte and tokenizer.model to /data/local/tmp/llama/ to run offline Llama 3.2 inference."
        }

        val sizeMb = "${pteFile.length() / (1024 * 1024)} MB"

        try {
            if (llmModule == null || loadedModelPath != pteFile.absolutePath) {
                val startLoad = System.nanoTime()
                val module = LlmModule(pteFile.absolutePath, tokFile.absolutePath, 0.7f)
                val loadStatus = module.load()
                val loadMs = (System.nanoTime() - startLoad) / 1_000_000L

                if (loadStatus != 0) {
                    PerformanceTracker.latestMetrics = RuntimeMetrics(
                        modelSizeMb = sizeMb,
                        loadTimeMs = "$loadMs ms",
                        ttftMs = "Error",
                        tokensPerSec = "0.0",
                        inferenceStatus = "ExecuTorch Load Error ($loadStatus)"
                    )
                    return@withContext "ExecuTorch failed to load model (status code $loadStatus). Check SELinux file permissions or model format."
                }

                llmModule = module
                loadedModelPath = pteFile.absolutePath
                PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                    modelSizeMb = sizeMb,
                    loadTimeMs = "$loadMs ms",
                    inferenceStatus = "ExecuTorch Active (Arm64)"
                )
            }

            val module = llmModule!!
            val formattedPrompt = buildLlama3Prompt(question, profile, difficulty, hintOnly)
            val responseBuilder = StringBuilder()
            val startGenNano = System.nanoTime()
            var firstTokenNano = 0L
            var tokenCount = 0

            val callback = object : LlmCallback {
                override fun onResult(result: String) {
                    if (result == "<|eot_id|>" || result == "<|end_of_text|>") return
                    if (tokenCount == 0) {
                        firstTokenNano = System.nanoTime()
                    }
                    tokenCount++
                    responseBuilder.append(result)
                }

                override fun onStats(stats: String) {
                    // Stats JSON received from ExecuTorch runtime
                }
            }

            module.generate(formattedPrompt, 256, callback, false)

            val endGenNano = System.nanoTime()
            val ttftMs = if (firstTokenNano > startGenNano) {
                (firstTokenNano - startGenNano) / 1_000_000L
            } else {
                0L
            }
            val totalGenSec = ((endGenNano - startGenNano).coerceAtLeast(1_000_000L)) / 1_000_000_000.0
            val measuredTps = tokenCount / totalGenSec

            PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                modelSizeMb = sizeMb,
                ttftMs = "$ttftMs ms",
                tokensPerSec = "%.1f".format(measuredTps),
                inferenceStatus = "ExecuTorch Active (Arm64)"
            )

            val finalResponse = responseBuilder.toString().trim()
            if (finalResponse.isNotEmpty()) {
                return@withContext finalResponse
            } else {
                return@withContext "Model generated an empty response. Try rephrasing your question."
            }
        } catch (t: Throwable) {
            PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                modelSizeMb = sizeMb,
                inferenceStatus = "Runtime Error: ${t.javaClass.simpleName}"
            )
            return@withContext "On-device inference error (${t.javaClass.simpleName}): ${t.message}"
        }
    }
}
