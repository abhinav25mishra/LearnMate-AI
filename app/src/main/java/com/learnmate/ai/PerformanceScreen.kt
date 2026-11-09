package com.learnmate.ai

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class InferenceMetrics(
    val modelName: String = "Llama-3.2-1B-Instruct (INT4)",
    val modelSizeMb: String = "Not loaded yet",
    val loadTimeMs: String = "-- ms",
    val ttftMs: String = "-- ms",
    val tokensPerSec: String = "-- tok/s",
    val inferenceStatus: String = "Standby (Mock Fallback Active)"
)

object PerformanceTracker {
    var latestMetrics by mutableStateOf(InferenceMetrics())
}

@Composable
fun PerformanceScreen() {
    val context = LocalContext.current
    val metrics = PerformanceTracker.latestMetrics

    val arch = remember { Build.SUPPORTED_ABIS.joinToString(", ") }
    val ramInfo = remember {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        val availMb = memInfo.availMem / (1024 * 1024)
        val totalMb = memInfo.totalMem / (1024 * 1024)
        "$availMb MB free / $totalMb MB total"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Technical Performance",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Live on-device hardware & ExecuTorch inference telemetry.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Device & Runtime", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Device Model: ${Build.MANUFACTURER} ${Build.MODEL}")
                Text("CPU Architecture: $arch")
                Text("System RAM: $ramInfo")
                Text("Offline Status: 100% Local (No Cloud APIs)")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ExecuTorch + Llama Metrics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Model: ${metrics.modelName}")
                Text("Model Size: ${metrics.modelSizeMb}")
                Text("Inference Status: ${metrics.inferenceStatus}")
                Text("Model Load Time: ${metrics.loadTimeMs}")
                Text("Time to First Token (TTFT): ${metrics.ttftMs}")
                Text("Generation Speed: ${metrics.tokensPerSec}")
            }
        }
    }
}