package com.learnmate.ai

object TopicGuardrail {

    private val domainLexicons: Map<String, Set<String>> = mapOf(
        "cybersecurity" to setOf(
            "cybersecurity", "cyber", "security", "encryption", "decrypt", "decryption", "symmetric",
            "asymmetric", "aes", "rsa", "sha", "md5", "hash", "hashing", "nmap", "scan", "scanning",
            "port", "ports", "ip", "subnet", "tcp", "udp", "icmp", "packet", "wireshark", "firewall",
            "ids", "ips", "vpn", "proxy", "ssl", "tls", "https", "certificate", "pki", "public key",
            "private key", "ssh", "authentication", "authorization", "2fa", "mfa", "oauth", "jwt",
            "malware", "ransomware", "trojan", "virus", "worm", "phishing", "social engineering",
            "xss", "csrf", "sql injection", "sqli", "buffer overflow", "zero day", "exploit",
            "vulnerability", "cve", "penetration", "pentest", "metasploit", "kali", "burp",
            "siem", "soc", "incident", "forensics", "cryptography", "cipher", "ceh", "owasp"
        ),
        "networking" to setOf(
            "network", "networking", "osi", "tcp", "udp", "ip", "ipv4", "ipv6", "subnet", "subnetting",
            "cidr", "router", "switch", "hub", "gateway", "dns", "dhcp", "http", "https", "ftp", "ssh",
            "arp", "icmp", "bgp", "ospf", "vlan", "nat", "mac", "ethernet", "wifi", "latency", "bandwidth"
        ),
        "data science" to setOf(
            "data science", "dataset", "pandas", "numpy", "matplotlib", "seaborn", "eda", "exploratory",
            "outlier", "missing value", "imputation", "correlation", "regression", "classification",
            "clustering", "hypothesis", "p-value", "distribution", "visualization", "feature", "model"
        ),
        "mathematics" to setOf(
            "math", "mathematics", "algebra", "geometry", "trigonometry", "calculus", "arithmetic",
            "equation", "formula", "number", "numbers", "integer", "fraction", "decimal", "percent",
            "percentage", "ratio", "proportion", "sum", "add", "addition", "subtract", "subtraction",
            "multiply", "multiplication", "divide", "division", "square", "root", "cube", "power",
            "exponent", "logarithm", "log", "matrix", "matrices", "determinant", "vector", "eigenvalue",
            "derivative", "differentiate", "integral", "integrate", "limit", "probability", "statistics",
            "mean", "median", "mode", "variance", "standard deviation", "permutation", "combination",
            "prime", "factor", "polynomial", "quadratic", "linear", "sin", "cos", "tan", "theta",
            "area", "volume", "perimeter", "radius", "diameter", "circle", "triangle", "angle", "solve",
            "calculate", "compute", "evaluate", "simplify", "prove", "theorem", "series", "sequence"
        ),
        "physics" to setOf(
            "physics", "force", "mass", "acceleration", "velocity", "speed", "displacement", "distance",
            "momentum", "energy", "kinetic", "potential", "work", "power", "gravity", "gravitation",
            "newton", "motion", "friction", "pressure", "density", "buoyancy", "fluid", "thermodynamics",
            "heat", "temperature", "entropy", "wave", "frequency", "wavelength", "amplitude", "sound",
            "light", "optics", "reflection", "refraction", "lens", "mirror", "electricity", "current",
            "voltage", "resistance", "ohm", "circuit", "capacitor", "inductor", "magnetism", "magnetic",
            "electromagnetic", "quantum", "atom", "nucleus", "electron", "photon", "relativity", "vector"
        ),
        "python" to setOf(
            "python", "py", "list", "tuple", "dict", "dictionary", "set", "string", "int", "float",
            "bool", "boolean", "function", "def", "return", "lambda", "class", "object", "self",
            "init", "inheritance", "loop", "for", "while", "if", "elif", "else", "try", "except",
            "finally", "import", "pip", "venv", "pandas", "numpy", "flask", "django", "fastapi",
            "comprehension", "generator", "decorator", "slice", "slicing", "index", "variable", "print",
            "input", "file", "json", "exception", "module", "package", "recursion", "algorithm", "code"
        ),
        "c programming" to setOf(
            "c", "c programming", "printf", "scanf", "pointer", "pointers", "malloc", "calloc",
            "realloc", "free", "struct", "union", "typedef", "enum", "header", "stdio", "stdlib",
            "string.h", "array", "loop", "for", "while", "do while", "switch", "function", "recursion",
            "variable", "int", "float", "char", "double", "void", "sizeof", "compile", "gcc", "macro"
        ),
        "data structures & algorithms" to setOf(
            "dsa", "data structure", "algorithm", "array", "linked list", "singly", "doubly", "circular",
            "stack", "queue", "deque", "priority queue", "heap", "tree", "binary tree", "bst", "avl",
            "traversal", "preorder", "inorder", "postorder", "bfs", "dfs", "graph", "dijkstra", "hash",
            "hashing", "collision", "sort", "sorting", "quicksort", "mergesort", "heapsort", "binary search",
            "recursion", "dynamic programming", "dp", "greedy", "backtracking", "big o", "complexity"
        ),
        "sql & databases" to setOf(
            "sql", "database", "dbms", "rdbms", "table", "row", "column", "select", "from", "where",
            "join", "inner join", "left join", "right join", "group by", "having", "order by", "insert",
            "update", "delete", "create", "alter", "drop", "primary key", "foreign key", "unique",
            "index", "normalization", "1nf", "2nf", "3nf", "acid", "transaction", "commit", "rollback",
            "window function", "rank", "coalesce", "null", "schema", "query", "postgres", "mysql", "sqlite"
        ),
        "machine learning & ai" to setOf(
            "machine learning", "ml", "ai", "artificial intelligence", "deep learning", "neural network",
            "regression", "classification", "clustering", "supervised", "unsupervised", "reinforcement",
            "gradient descent", "backpropagation", "overfitting", "underfitting", "regularization",
            "precision", "recall", "f1", "accuracy", "roc", "auc", "confusion matrix", "feature",
            "scaling", "pca", "random forest", "decision tree", "svm", "knn", "transformer", "attention",
            "llm", "quantization", "pytorch", "tensorflow", "scikit", "dataset", "training", "validation"
        ),
        "history" to setOf(
            "history", "historical", "empire", "dynasty", "king", "emperor", "queen", "war", "battle",
            "revolution", "independence", "ancient", "medieval", "modern", "civilization", "mughal",
            "akbar", "ashoka", "gupta", "maurya", "british", "treaty", "constitution", "century",
            "timeline", "monument", "heritage", "freedom", "struggle", "world war", "renaissance"
        ),
        "geography" to setOf(
            "geography", "earth", "continent", "ocean", "river", "mountain", "plateau", "climate",
            "monsoon", "weather", "latitude", "longitude", "equator", "hemisphere", "soil", "forest",
            "glacier", "volcano", "earthquake", "tectonic", "atmosphere", "biosphere", "map", "region",
            "country", "capital", "population", "resource", "agriculture", "delta", "peninsula"
        )
    )

    private val nonAcademicTrivialTerms: Set<String> = setOf(
        "capital of", "prime minister", "president of", "who is the father of",
        "cricket", "football", "fifa", "ipl", "bollywood", "hollywood", "movie", "song",
        "recipe", "cooking", "biryani", "horoscope", "astrology", "zodiac", "celebrity", "joke"
    )

    private val conversationalFollowUps = setOf(
        "explain", "example", "examples", "why", "how", "what", "define", "definition", "difference",
        "compare", "summary", "summarize", "detail", "details", "step by step", "hint", "practice",
        "question", "quiz", "test", "more", "again", "clarify", "simplify", "code", "program", "roadmap"
    )

    fun isQuestionAllowed(
        question: String,
        selectedTopic: String,
        extraAllowedTopics: List<String> = emptyList()
    ): Boolean {
        val qLower = question.trim().lowercase()
        val topicLower = selectedTopic.trim().lowercase()
        if (qLower.isEmpty() || topicLower.isEmpty()) return true

        // 1. Direct match against active subject or its subtopics
        if (qLower.contains(topicLower)) return true
        val topicTokens = topicLower.split(Regex("[^a-z0-9+#]+")).filter { it.length >= 2 }
        if (topicTokens.any { token -> Regex("\\b${Regex.escape(token)}\\b").containsMatchIn(qLower) }) {
            return true
        }
        if (extraAllowedTopics.any { sub -> sub.isNotBlank() && qLower.contains(sub.trim().lowercase()) }) {
            return true
        }

        // 2. Quantitative / arithmetic expressions when studying Mathematics
        if (topicLower.contains("math")) {
            val hasDigit = qLower.any { it.isDigit() }
            val hasMathSymbol = qLower.any { it in charArrayOf('+', '-', '*', '/', '=', '^', '%', '√', 'π', '<', '>') }
            if (hasDigit && (hasMathSymbol || qLower.length <= 24)) {
                return true
            }
        }

        // 3. Reject obvious non-academic trivia when it does not match the active subject
        val isTrivialOffTopic = nonAcademicTrivialTerms.any { phrase ->
            !topicLower.contains(phrase) && qLower.contains(phrase)
        }
        if (isTrivialOffTopic) return false

        // 4. Check domain lexicon for the active subject
        val activeLexicon = domainLexicons.entries.firstOrNull { (domainKey, _) ->
            topicLower.contains(domainKey) || domainKey.contains(topicLower)
        }?.value

        if (activeLexicon != null && activeLexicon.any { kw -> Regex("\\b${Regex.escape(kw)}\\b").containsMatchIn(qLower) }) {
            return true
        }

        // 5. Reject if the question clearly belongs to a completely different preset domain
        val hitsUnrelatedDomain = domainLexicons.any { (otherDomain, otherKeywords) ->
            !topicLower.contains(otherDomain) && !otherDomain.contains(topicLower) &&
                otherKeywords.any { kw -> kw.length >= 4 && Regex("\\b${Regex.escape(kw)}\\b").containsMatchIn(qLower) }
        }
        if (hitsUnrelatedDomain) return false

        // 6. Custom subjects not in the preset map pass directly to Llama unless flagged off-topic above
        if (activeLexicon == null) {
            return true
        }

        // 7. Short contextual follow-ups within the active conversation
        val words = qLower.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size <= 6 && words.any { it in conversationalFollowUps }) {
            return true
        }

        return false
    }

    fun buildRefusalMessage(selectedTopic: String): String {
        return "I can only help with questions related to $selectedTopic. Please ask a question about $selectedTopic."
    }
}
