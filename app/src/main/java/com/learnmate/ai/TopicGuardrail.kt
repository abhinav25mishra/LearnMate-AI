package com.learnmate.ai

object TopicGuardrail {

    private val domainKeywords: Map<String, Set<String>> = mapOf(
"c programming" to setOf(
            "c", "c programming", "printf", "scanf", "pointer", "pointers", "malloc", "calloc",
            "free", "struct", "union", "typedef", "header", "stdio", "stdlib", "string.h",
            "array", "loop", "for", "while", "do while", "switch", "function", "recursion",
            "variable", "int", "float", "char", "double", "void", "sizeof", "compile", "gcc"
        ),
        "python" to setOf(
            "python", "py", "pip", "def", "class", "list", "tuple", "dict", "dictionary",
            "set", "loop", "for", "while", "if", "else", "elif", "function", "lambda",
            "import", "module", "package", "pandas", "numpy", "flask", "django", "fastapi",
            "exception", "try", "except", "string", "int", "float", "boolean", "print",
            "input", "file", "json", "comprehension", "generator", "decorator", "oop",
            "inheritance", "recursion", "variable", "syntax", "code", "program", "script",
            "debug", "error", "compile", "interpreter", "array", "method", "return"
        ),
        "data structures" to setOf(
            "data structure", "dsa", "array", "list", "linked list", "singly", "doubly",
            "circular", "stack", "queue", "deque", "tree", "binary tree", "bst", "avl",
            "heap", "graph", "hash", "hashmap", "hashtable", "trie", "node", "pointer",
            "recursion", "sorting", "searching", "binary search", "quick sort", "merge sort",
            "bubble sort", "complexity", "big o", "time complexity", "space complexity",
            "traversal", "preorder", "inorder", "postorder", "bfs", "dfs", "algorithm", "code"
        ),
        "sql" to setOf(
            "sql", "database", "dbms", "table", "query", "select", "insert", "update",
            "delete", "join", "inner join", "left join", "right join", "group by", "order by",
            "having", "where", "index", "primary key", "foreign key", "normalization",
            "transaction", "acid", "postgres", "mysql", "sqlite", "nosql", "schema", "view"
        ),
        "machine learning" to setOf(
            "machine learning", "ml", "ai", "artificial intelligence", "deep learning",
            "neural network", "model", "training", "dataset", "feature", "regression",
            "classification", "clustering", "k-means", "decision tree", "random forest",
            "gradient descent", "overfitting", "underfitting", "accuracy", "precision",
            "recall", "tensor", "pytorch", "tensorflow", "quantization", "llm", "transformer"
        ),
        "mathematics" to setOf(
            "math", "mathematics", "algebra", "calculus", "matrix", "matrices", "determinant",
            "vector", "probability", "statistics", "mean", "median", "variance", "deviation",
            "distribution", "permutation", "combination", "equation", "derivative", "integral",
            "function", "limit", "theorem", "proof", "trigonometry", "geometry", "linear",
            "regression", "correlation", "number", "solve", "formula"
        ),
        "data engineering" to setOf(
            "data engineer", "data engineering", "etl", "elt", "pipeline", "airflow", "spark",
            "pyspark", "kafka", "warehouse", "snowflake", "bigquery", "redshift", "parquet",
            "avro", "sql", "python", "batch", "streaming", "lakehouse", "databricks", "schema"
        )
    )

    private val conversationalFollowUps = setOf(
        "explain more", "give an example", "show example", "practice problem",
        "give me a hint", "simplify", "why", "how does it work", "code example",
        "step by step", "next step", "quiz me", "test me"
    )

    fun buildRefusalMessage(selectedTopic: String): String {
        return "I can only help with questions related to $selectedTopic. Please ask a question about $selectedTopic."
    }

    fun isQuestionAllowed(
        rawQuestion: String,
        selectedTopic: String,
        extraContextTopics: List<String> = emptyList()
    ): Boolean {
        val cleanQuestion = rawQuestion.substringAfter("]").trim().ifEmpty { rawQuestion.trim() }
        val qLower = cleanQuestion.lowercase()
        val topicLower = selectedTopic.trim().lowercase()

        if (topicLower.isEmpty() || topicLower == "general") return true

        // 1. Direct mention of the selected topic or its words (length >= 3)
        if (qLower.contains(topicLower)) return true
        val topicTokens = topicLower.split(Regex("[^a-z0-9]+")).filter { it.length >= 3 }
        if (topicTokens.any { token -> Regex("\\b${Regex.escape(token)}\\b").containsMatchIn(qLower) }) {
            return true
        }

        // 2. Check domain-specific keyword dictionary matching selectedTopic
        val matchedDomainKeywords = domainKeywords.entries
            .filter { (domainKey, _) -> topicLower.contains(domainKey) || domainKey.contains(topicLower) }
            .flatMap { it.value }
            .toSet()

        if (matchedDomainKeywords.any { kw ->
                if (kw.contains(" ")) qLower.contains(kw)
                else Regex("\\b${Regex.escape(kw)}\\b").containsMatchIn(qLower)
            }
        ) {
            return true
        }

        // 3. Check active sub-topics / weak topics from the user's current roadmap
        val subTokens = extraContextTopics
            .flatMap { it.lowercase().split(Regex("[^a-z0-9]+")) }
            .filter { it.length >= 4 }
            .toSet()
        if (subTokens.any { token -> Regex("\\b${Regex.escape(token)}\\b").containsMatchIn(qLower) }) {
            return true
        }

        // 4. Allow short follow-up prompts only if they don't introduce unrelated proper nouns/entities
        if (conversationalFollowUps.any { qLower == it || qLower.startsWith("$it ") }) {
            return true
        }

        return false
    }
}
