package com.learnmate.ai

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
        val cleanQuestion = question.substringAfter("]").trim().ifEmpty { question.trim() }
        val modeNote = if (hintOnly) "Give a short hint first." else "Give a clear step-by-step answer with example."
        val sys = "You are LearnMate AI, a helpful ${profile.subject} tutor ($difficulty level, ${profile.preferredLanguage}). $modeNote"

        // Do NOT prepend <|begin_of_text|> because ExecuTorch LlmModule prepends BOS automatically
        return "<|start_header_id|>system<|end_header_id|>\n\n" +
            "$sys<|eot_id|><|start_header_id|>user<|end_header_id|>\n\n" +
            "$cleanQuestion<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n"
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
            return@withContext "Model files not found on device. Push llama3_2_1b_int4.pte and tokenizer.model to /data/local/tmp/llama/."
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
                    return@withContext "ExecuTorch failed to load model (code $loadStatus)."
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
            var rawStatsJson = ""

            val callback = object : LlmCallback {
                override fun onResult(result: String) {
                    if (result.isEmpty()) return
                    if (result == "<|eot_id|>" || result == "<|end_of_text|>" || result == "<|begin_of_text|>") return
                    if (tokenCount == 0) {
                        firstTokenNano = System.nanoTime()
                    }
                    tokenCount++
                    responseBuilder.append(result)
                }

                override fun onStats(stats: String) {
                    rawStatsJson = stats
                    try {
                        val json = JSONObject(stats)
                        val tps = json.optDouble("tokens_per_second", -1.0)
                        val ttft = json.optLong("first_token_ms", -1L)
                        if (tps > 0.0) {
                            PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                                tokensPerSec = "%.1f".format(tps),
                                inferenceStatus = "ExecuTorch Active (Arm64)"
                            )
                        }
                        if (ttft > 0L) {
                            PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                                ttftMs = "$ttft ms"
                            )
                        }
                    } catch (_: Exception) {
                    }
                }
            }

            // Use seqLen = 512 so prompt + generated tokens have plenty of context window
            val genStatus = module.generate(formattedPrompt, 512, callback, false)

            val endGenNano = System.nanoTime()
            if (tokenCount > 0) {
                val ttftMs = if (firstTokenNano > startGenNano) (firstTokenNano - startGenNano) / 1_000_000L else 0L
                val totalGenSec = ((endGenNano - startGenNano).coerceAtLeast(1_000_000L)) / 1_000_000_000.0
                val measuredTps = tokenCount / totalGenSec

                if (PerformanceTracker.latestMetrics.ttftMs == "0 ms") {
                    PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(ttftMs = "$ttftMs ms")
                }
                if (PerformanceTracker.latestMetrics.tokensPerSec == "0.0") {
                    PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(tokensPerSec = "%.1f".format(measuredTps))
                }
            }

            val finalResponse = responseBuilder.toString()
                .replace("<|eot_id|>", "")
                .replace("<|end_of_text|>", "")
                .trim()

            if (finalResponse.isNotEmpty()) {
                return@withContext finalResponse
            }

            // Fallback: try plain prompt without Llama 3 header tags in case .pte uses base/custom template
            val plainPrompt = "Question: ${question.substringAfter("]").trim()}\nAnswer:"
            module.generate(plainPrompt, 512, callback, false)

            val retryResponse = responseBuilder.toString()
                .replace("<|eot_id|>", "")
                .replace("<|end_of_text|>", "")
                .trim()

            if (retryResponse.isNotEmpty()) {
                return@withContext retryResponse
            }

            return@withContext "ExecuTorch returned 0 tokens (status=$genStatus, stats=$rawStatsJson). Verify tokenizer.model matches llama3_2_1b_int4.pte."
        } catch (t: Throwable) {
            PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                modelSizeMb = sizeMb,
                inferenceStatus = "Runtime Error: ${t.javaClass.simpleName}"
            )
            return@withContext "On-device inference error (${t.javaClass.simpleName}): ${t.message}"
        }
    }
}
