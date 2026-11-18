package com.learnmate.ai

import android.util.Log
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
            File("/data/local/tmp/llama/tokenizer.json"),
            File("/sdcard/Android/data/com.learnmate.ai/files/llama/tokenizer.model"),
            File("/sdcard/Android/data/com.learnmate.ai/files/llama/tokenizer.json")
        )
        val pte = pteCandidates.firstOrNull { it.exists() && it.length() > 10_000_000L }
        val tok = tokCandidates.firstOrNull { it.exists() && it.length() > 100_000L }
        return Pair(pte, tok)
    }

    private fun buildCompactPrompt(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String {
        val cleanQuestion = question.substringAfter("]").trim().ifEmpty { question.trim() }
        val modeNote = if (hintOnly) "Give a brief hint." else "Answer clearly with a short example."
        // Keep prompt short (~35 tokens) so prefill finishes in <2 seconds on mobile RAM
        return "<|start_header_id|>system<|end_header_id|>\n\n" +
            "You are a ${profile.subject} tutor ($difficulty, ${profile.preferredLanguage}). $modeNote<|eot_id|>" +
            "<|start_header_id|>user<|end_header_id|>\n\n" +
            "$cleanQuestion<|eot_id|>" +
            "<|start_header_id|>assistant<|end_header_id|>\n\n"
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
                inferenceStatus = "Model Files Missing"
            )
            return@withContext "Model files not found in /data/local/tmp/llama/."
        }

        val sizeMb = "${pteFile.length() / (1024 * 1024)} MB"

        try {
            if (llmModule == null || loadedModelPath != pteFile.absolutePath) {
                Log.d("LearnMateLLM", "Loading model=${pteFile.absolutePath} tok=${tokFile.absolutePath}")
                val startLoad = System.nanoTime()
                val module = LlmModule(pteFile.absolutePath, tokFile.absolutePath, 0.7f)
                val loadStatus = module.load()
                val loadMs = (System.nanoTime() - startLoad) / 1_000_000L
                Log.d("LearnMateLLM", "Load finished status=$loadStatus in ${loadMs}ms")

                if (loadStatus != 0) {
                    PerformanceTracker.latestMetrics = RuntimeMetrics(
                        modelSizeMb = sizeMb,
                        loadTimeMs = "$loadMs ms",
                        ttftMs = "Error",
                        tokensPerSec = "0.0",
                        inferenceStatus = "ExecuTorch Load Error ($loadStatus)"
                    )
                    return@withContext "ExecuTorch load failed with code $loadStatus."
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
            val formattedPrompt = buildCompactPrompt(question, profile, difficulty, hintOnly)
            val responseBuilder = StringBuilder()
            val startGenNano = System.nanoTime()
            var firstTokenNano = 0L
            var tokenCount = 0
            var rawStatsJson = ""

            val callback = object : LlmCallback {
                override fun onResult(result: String) {
                    Log.d("LearnMateLLM", "onResult token[$tokenCount]='$result'")
                    if (result.isEmpty()) return
                    if (result == "<|eot_id|>" || result == "<|end_of_text|>" || result == "<|begin_of_text|>") return
                    if (tokenCount == 0) {
                        firstTokenNano = System.nanoTime()
                    }
                    tokenCount++
                    responseBuilder.append(result)
                }

                override fun onStats(stats: String) {
                    Log.d("LearnMateLLM", "onStats=$stats")
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

            // Cap seqLen to 128 so prefill + generation finishes quickly without RAM swap stall
            val genStatus = module.generate(formattedPrompt, 128, callback, false)
            Log.d("LearnMateLLM", "generate finished status=$genStatus tokens=$tokenCount stats=$rawStatsJson")

            val endGenNano = System.nanoTime()
            val ttftMs = if (firstTokenNano > startGenNano) (firstTokenNano - startGenNano) / 1_000_000L else 0L
            val totalGenSec = ((endGenNano - startGenNano).coerceAtLeast(1_000_000L)) / 1_000_000_000.0
            val measuredTps = tokenCount / totalGenSec

            if (tokenCount > 0) {
                PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                    modelSizeMb = sizeMb,
                    ttftMs = if (PerformanceTracker.latestMetrics.ttftMs == "0 ms") "$ttftMs ms" else PerformanceTracker.latestMetrics.ttftMs,
                    tokensPerSec = if (PerformanceTracker.latestMetrics.tokensPerSec == "0.0") "%.1f".format(measuredTps) else PerformanceTracker.latestMetrics.tokensPerSec,
                    inferenceStatus = "ExecuTorch Active (Arm64)"
                )
            }

            val finalResponse = responseBuilder.toString()
                .replace("<|eot_id|>", "")
                .replace("<|end_of_text|>", "")
                .trim()

            if (finalResponse.isNotEmpty()) {
                return@withContext finalResponse
            }

            return@withContext "ExecuTorch status=$genStatus, tokens=$tokenCount, stats=$rawStatsJson"
        } catch (t: Throwable) {
            Log.e("LearnMateLLM", "Inference exception", t)
            PerformanceTracker.latestMetrics = PerformanceTracker.latestMetrics.copy(
                modelSizeMb = sizeMb,
                inferenceStatus = "Runtime Error: ${t.javaClass.simpleName}"
            )
            return@withContext "On-device inference error (${t.javaClass.simpleName}): ${t.message}"
        }
    }
}
