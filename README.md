# VicharaX — 24×7 Offline On-Device AI Tutor (Arm64 Edge)

**VicharaX** is an offline-first Android AI learning mentor built with **Kotlin**, **Jetpack Compose**, and **PyTorch ExecuTorch** running **Llama 3.2 1B (INT4 quantized)** directly on mobile Arm64 CPUs—requiring zero internet connectivity or cloud API tokens.

## Key Features
- **On-Device Neural Inference:** Executes `Llama-3.2-1B-Instruct.pte` via PyTorch ExecuTorch on Arm64 with a fast offline fallback engine.
- **Zero-Token Topic Guardrail (`TopicGuardrail.kt`):** Validates query relevance in `<1 ms` before invoking LLM inference, blocking unrelated trivia with zero wasted compute tokens while supporting any preset or custom subject (*Cybersecurity, Data Science, Networking, Mathematics, Physics, Python, C, SQL, DSA*, etc.).
- **2-Step Student Profile Intake & Local Persistence:** Offline Username + PIN authentication (`ProfileStorage`), standard-based subject recommendations, custom topic search, and multilingual UI support (*English, Hindi, Bengali, Telugu, Tamil, Marathi, Kannada, Gujarati*).
- **Dynamic Sequential Roadmap & 10-MCQ Assessment:** Generates topic-specific sequential study stages with curated reference links, shuffled A/B/C/D diagnostic quizzes, post-submission answer corrections, and an automated **Learning Improvement Graph**.

## Tech & Hardware Stack
- **Target Architecture:** Android Arm64 (`arm64-v8a`, tested on Vivo V2311)
- **Model & Quantization:** Meta Llama 3.2 1B Instruct (`INT4` quantized `.pte` format via ExecuTorch)
- **UI & Persistence:** Kotlin, Jetpack Compose (Material 3), and local JSON `SharedPreferences`
