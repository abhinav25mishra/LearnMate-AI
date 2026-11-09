package com.learnmate.ai

data class UiStrings(
    val offlineBadge: String,
    val tabHome: String,
    val tabRoadmap: String,
    val tabMentor: String,
    val tabPerf: String,
    val welcomeBack: String,
    val quickActions: String,
    val studyRoadmap: String,
    val diagnosticDesc: String,
    val aiMentor: String,
    val askDoubtsDesc: String,
    val learningProfile: String,
    val resetProfile: String
)

object StringsManager {
    fun getStrings(language: String): UiStrings {
        return when (language) {
            "Hindi" -> UiStrings(
                offlineBadge = "100% ऑफ़लाइन",
                tabHome = "होम",
                tabRoadmap = "रोडमैप",
                tabMentor = "AI मेंटर",
                tabPerf = "परफॉरमेंस",
                welcomeBack = "वापसी पर स्वागत है",
                quickActions = "त्वरित कार्य",
                studyRoadmap = "अध्ययन रोडमैप",
                diagnosticDesc = "डायग्नोस्टिक और दैनिक योजना",
                aiMentor = "AI मेंटर",
                askDoubtsDesc = "ऑफ़लाइन संदेह पूछें",
                learningProfile = "लर्निंग प्रोफ़ाइल",
                resetProfile = "प्रोफ़ाइल बदलें / रीसेट करें"
            )
            "Tamil" -> UiStrings(
                offlineBadge = "100% ஆஃப்லைன்",
                tabHome = "முகப்பு",
                tabRoadmap = "வழிகாட்டி",
                tabMentor = "AI ஆசான்",
                tabPerf = "செயல்திறன்",
                welcomeBack = "நல்வரவு",
                quickActions = "விரைவு செயல்கள்",
                studyRoadmap = "கற்றல் பாதை",
                diagnosticDesc = "திட்டமிடல் & சோதனை",
                aiMentor = "AI ஆசான்",
                askDoubtsDesc = "சந்தேகங்களைக் கேளுங்கள்",
                learningProfile = "கற்றல் சுயவிவரம்",
                resetProfile = "சுயவிவரத்தை மாற்று"
            )
            "Telugu" -> UiStrings(
                offlineBadge = "100% ఆఫ్‌లైన్",
                tabHome = "హోమ్",
                tabRoadmap = "రోడ్‌మ్యాప్",
                tabMentor = "AI మెంటర్",
                tabPerf = "పనితీరు",
                welcomeBack = "తిరిగి స్వాగతం",
                quickActions = "త్వరిత చర్యలు",
                studyRoadmap = "స్టడీ రోడ్‌మ్యాప్",
                diagnosticDesc = "రోజువారీ ప్రణాళిక",
                aiMentor = "AI మెంటర్",
                askDoubtsDesc = "ఆఫ్‌లైన్‌లో సందేహాలు అడగండి",
                learningProfile = "లెర్నింగ్ ప్రొఫైల్",
                resetProfile = "ప్రొఫైల్ మార్చండి"
            )
            "Bengali" -> UiStrings(
                offlineBadge = "100% অফলাইন",
                tabHome = "হোম",
                tabRoadmap = "রোডম্যাপ",
                tabMentor = "AI মেন্টর",
                tabPerf = "পারফরম্যান্স",
                welcomeBack = "স্বাগতম",
                quickActions = "দ্রুত অ্যাকশন",
                studyRoadmap = "স্টাডি রোডম্যাপ",
                diagnosticDesc = "দৈনিক পরিকল্পনা",
                aiMentor = "AI মেন্টর",
                askDoubtsDesc = "অফলাইনে প্রশ্ন করুন",
                learningProfile = "লার্নিং প্রোফাইল",
                resetProfile = "প্রোফাইল পরিবর্তন করুন"
            )
            "Marathi" -> UiStrings(
                offlineBadge = "100% ऑफलाइन",
                tabHome = "होम",
                tabRoadmap = "रोडमॅप",
                tabMentor = "AI मेंटर",
                tabPerf = "कामगिरी",
                welcomeBack = "पुन्हा स्वागत आहे",
                quickActions = "त्वरित कृती",
                studyRoadmap = "अभ्यास रोडमॅप",
                diagnosticDesc = "दैनिक नियोजन",
                aiMentor = "AI मेंटर",
                askDoubtsDesc = "ऑफलाइन शंका विचारा",
                learningProfile = "लर्निंग प्रोफाइल",
                resetProfile = "प्रोफाइल बदला / रीसेट करा"
            )
            else -> UiStrings(
                offlineBadge = "100% Offline",
                tabHome = "Home",
                tabRoadmap = "Roadmap",
                tabMentor = "AI Mentor",
                tabPerf = "Perf",
                welcomeBack = "Welcome back",
                quickActions = "Quick Actions",
                studyRoadmap = "Study Roadmap",
                diagnosticDesc = "Diagnostic & daily plan",
                aiMentor = "AI Mentor",
                askDoubtsDesc = "Ask doubts offline",
                learningProfile = "Learning Profile",
                resetProfile = "Edit / Reset Profile"
            )
        }
    }
}