package com.learnmate.ai

object TopicGuardrail {

    private val domainKeywords: Map<String, Set<String>> = mapOf(
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

    private val conversationalFollowUps = setOf(
        "explain", "example", "examples", "why", "how", "what", "define", "definition", "difference",
        "compare", "summary", "summarize", "detail", "details", "step by step", "hint", "practice",
        "question", "quiz", "test", "more", "again", "clarify", "simplify", "code", "program"
    )

    fun isQuestionAllowed(
        question: String,
        selectedTopic: String,
        extraAllowedTopics: List<String> = emptyList()
    ): Boolean {
        val qLower = question.trim().lowercase()
        val topicLower = selectedTopic.trim().lowercase()
        if (qLower.isEmpty() || topicLower.isEmpty()) return true

        // 1. Direct mention of selected topic or any subtopic
        if (qLower.contains(topicLower)) return true
        val topicTokens = topicLower.split(Regex("[^a-z0-9+#]+")).filter { it.length >= 2 }
        if (topicTokens.any { token -> Regex("\\b${Regex.escape(token)}\\b").containsMatchIn(qLower) }) {
            return true
        }
        if (extraAllowedTopics.any { sub -> sub.isNotBlank() && qLower.contains(sub.trim().lowercase()) }) {
            return true
        }

        // 2. Special handling for Mathematics: allow arithmetic expressions, numbers, and math symbols (e.g., "2 + 2", "5*8", "x^2 = 9")
        if (topicLower.contains("math")) {
            val hasDigit = qLower.any { it.isDigit() }
            val hasMathOperator = qLower.any { it in charArrayOf('+', '-', '*', '/', '=', '^', '%', '√', 'π', '<', '>') }
            if (hasDigit && (hasMathOperator || qLower.length <= 24)) {
                return true
            }
        }

        // 3. Special handling for Physics: allow numerical/unit/formula expressions (e.g., "F = ma", "9.8 m/s^2", "10 N")
        if (topicLower.contains("physic")) {
            val hasPhysicsPattern = Regex("\\b(f\\s*=|v\\s*=|e\\s*=|m/s|kg|newton|joule|watt|volt|amp|ohm|hz)\\b").containsMatchIn(qLower)
            if (hasPhysicsPattern) return true
        }

        // 4. Domain keyword bank matching for the active topic
        val matchedBank = domainKeywords.entries.firstOrNull { (domainKey, _) ->
            topicLower.contains(domainKey) || domainKey.contains(topicLower)
        }?.value

        if (matchedBank != null) {
            if (matchedBank.any { kw -> Regex("\\b${Regex.escape(kw)}\\b").containsMatchIn(qLower) }) {
                return true
            }
            // Reject if it clearly belongs to a DIFFERENT known domain
            val belongsToOtherDomain = domainKeywords.any { (otherDomain, otherKeywords) ->
                !topicLower.contains(otherDomain) && !otherDomain.contains(topicLower) &&
                    otherKeywords.any { kw -> kw.length >= 4 && Regex("\\b${Regex.escape(kw)}\\b").containsMatchIn(qLower) }
            }
            if (belongsToOtherDomain) return false
        }

        // 5. Allow short contextual follow-ups ("give an example", "explain step by step") if not referencing another domain
        val words = qLower.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size <= 5 && words.any { it in conversationalFollowUps }) {
            val hitsOther = domainKeywords.any { (otherDomain, otherKeywords) ->
                !topicLower.contains(otherDomain) && !otherDomain.contains(topicLower) &&
                    otherKeywords.any { kw -> kw.length >= 4 && Regex("\\b${Regex.escape(kw)}\\b").containsMatchIn(qLower) }
            }
            if (!hitsOther) return true
        }

        return false
    }

    fun buildRefusalMessage(selectedTopic: String): String {
        return "I can only help with questions related to $selectedTopic. Please ask a question about $selectedTopic."
    }
}
