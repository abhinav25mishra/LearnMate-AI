package com.learnmate.ai

data class DiagnosticQuestion(
    val id: Int,
    val topic: String,
    val prerequisite: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class KnowledgeGapReport(
    val assignedLevel: String,
    val strongTopics: List<String>,
    val weakTopics: List<String>,
    val missingPrerequisites: List<String>
)

data class RoadmapModule(
    val id: String,
    val title: String,
    val prerequisite: String,
    val dailyTask: String,
    val estimatedMinutes: Int,
    val isRevision: Boolean = false,
    val isCompleted: Boolean = false
)

object LearningEngine {

    fun getDiagnosticQuestions(subject: String): List<DiagnosticQuestion> {
        val subjLower = subject.lowercase()

        if (subjLower.contains("python")) {
            return listOf(
                DiagnosticQuestion(1, "Data Types & Literals", "Memory & Primitive Types",
                    "Which built-in Python type stores immutable ordered sequences of elements?",
                    listOf("list", "tuple", "dict", "set"), 1),
                DiagnosticQuestion(2, "Control Flow & Loops", "Boolean Expressions",
                    "Which keyword immediately exits the nearest enclosing loop in Python?",
                    listOf("continue", "pass", "break", "return"), 2),
                DiagnosticQuestion(3, "Functions & Parameters", "Variable Scope",
                    "What does a Python function return by default if no return statement is executed?",
                    listOf("0", "False", "None", "Empty string"), 2),
                DiagnosticQuestion(4, "Lists & Slicing", "0-Based Indexing",
                    "What is the output of [10, 20, 30, 40][1:3] in Python?",
                    listOf("[10, 20]", "[20, 30]", "[20, 30, 40]", "[10, 20, 30]"), 1),
                DiagnosticQuestion(5, "Dictionaries & Hashing", "Key-Value Mapping",
                    "Which dictionary method safely retrieves a value without raising KeyError if the key is missing?",
                    listOf("dict.fetch()", "dict.get()", "dict.find()", "dict.lookup()"), 1),
                DiagnosticQuestion(6, "List Comprehensions", "Iteration & Filtering",
                    "Which expression creates a list of squares for even numbers from 0 to 9?",
                    listOf("[x*x for x in range(10) if x % 2 == 0]", "{x*x : x in range(10)}", "(x^2 for x in 10)", "[if x%2==0: x*x]"), 0),
                DiagnosticQuestion(7, "Exception Handling", "Runtime Error Flow",
                    "Which block in Python always executes whether an exception occurred or not?",
                    listOf("except", "else", "finally", "catch"), 2),
                DiagnosticQuestion(8, "Object-Oriented Python", "Classes & Instances",
                    "What is the conventional name of the first parameter in a Python instance method?",
                    listOf("this", "self", "cls", "obj"), 1),
                DiagnosticQuestion(9, "Modules & Environments", "Package Management",
                    "Which command-line tool installs third-party packages from PyPI?",
                    listOf("pip", "npm", "cargo", "gradle"), 0),
                DiagnosticQuestion(10, "Algorithmic Complexity", "Big-O Fundamentals",
                    "What is the average-case time complexity of checking membership (x in my_set) in a Python set?",
                    listOf("O(n)", "O(log n)", "O(1)", "O(n log n)"), 2)
            )
        }

        if (subjLower.contains("data structure") || subjLower.contains("dsa") || subjLower.contains("algorithm")) {
            return listOf(
                DiagnosticQuestion(1, "Arrays & Indexing", "Contiguous Memory",
                    "What is the time complexity of accessing an element by index in an array?",
                    listOf("O(n)", "O(1)", "O(log n)", "O(n^2)"), 1),
                DiagnosticQuestion(2, "Linked Lists", "Pointers & Dynamic Nodes",
                    "What is the time complexity of inserting a node at the head of a singly linked list?",
                    listOf("O(n)", "O(log n)", "O(1)", "O(n log n)"), 2),
                DiagnosticQuestion(3, "Stacks", "LIFO Principle",
                    "Which data structure follows the Last-In, First-Out (LIFO) ordering?",
                    listOf("Queue", "Stack", "Heap", "Graph"), 1),
                DiagnosticQuestion(4, "Queues", "FIFO Principle",
                    "Which traversal algorithm uses a Queue to explore a graph level by level?",
                    listOf("Depth-First Search (DFS)", "Breadth-First Search (BFS)", "Binary Search", "Quick Sort"), 1),
                DiagnosticQuestion(5, "Binary Search", "Divide & Conquer",
                    "What prerequisite must hold before running Binary Search on a list?",
                    listOf("All values must be positive", "The list must be sorted", "Length must be a power of 2", "No duplicate values"), 1),
                DiagnosticQuestion(6, "Trees & Traversals", "Recursion Basics",
                    "Which traversal of a Binary Search Tree (BST) visits nodes in sorted ascending order?",
                    listOf("Preorder", "Postorder", "Inorder", "Level-order"), 2),
                DiagnosticQuestion(7, "AVL & Balanced Trees", "Tree Height & Rotations",
                    "What is the allowed balance factor range for every node in an AVL tree?",
                    listOf("0 only", "-1, 0, +1", "-2 to +2", "Any integer"), 1),
                DiagnosticQuestion(8, "Hash Tables", "Hash Functions & Collisions",
                    "Which technique resolves hash collisions by storing a linked list at each bucket?",
                    listOf("Linear Probing", "Separate Chaining", "Double Hashing", "Binary Lifting"), 1),
                DiagnosticQuestion(9, "Sorting Algorithms", "Recursion & Partitioning",
                    "What is the worst-case time complexity of Merge Sort?",
                    listOf("O(n^2)", "O(n)", "O(n log n)", "O(log n)"), 2),
                DiagnosticQuestion(10, "Heaps & Priority Queues", "Complete Binary Trees",
                    "Where is the minimum element located in a standard Min-Heap?",
                    listOf("Rightmost leaf", "Root node", "Middle level", "Leftmost leaf"), 1)
            )
        }

        if (subjLower.contains("sql") || subjLower.contains("database") || subjLower.contains("dbms")) {
            return listOf(
                DiagnosticQuestion(1, "SELECT & Filtering", "Relational Tables",
                    "Which SQL clause filters rows before aggregation takes place?",
                    listOf("HAVING", "WHERE", "GROUP BY", "ORDER BY"), 1),
                DiagnosticQuestion(2, "SQL Joins", "Foreign Keys",
                    "Which join returns all rows from the left table and matched rows from the right table?",
                    listOf("INNER JOIN", "LEFT JOIN", "CROSS JOIN", "SELF JOIN"), 1),
                DiagnosticQuestion(3, "Aggregations & Grouping", "Aggregate Functions",
                    "Which clause filters aggregated groups created by GROUP BY?",
                    listOf("WHERE", "FILTER", "HAVING", "DISTINCT"), 2),
                DiagnosticQuestion(4, "Primary & Foreign Keys", "Entity Integrity",
                    "Which constraint guarantees every value in a column is unique and non-null?",
                    listOf("FOREIGN KEY", "CHECK", "PRIMARY KEY", "DEFAULT"), 2),
                DiagnosticQuestion(5, "Database Normalization", "Functional Dependencies",
                    "Which normal form eliminates transitive dependencies on non-key attributes?",
                    listOf("1NF", "2NF", "3NF", "UNF"), 2),
                DiagnosticQuestion(6, "Indexes & Query Tuning", "B-Tree Lookup",
                    "What is the primary benefit of creating an index on a frequently queried column?",
                    listOf("Reduces disk storage", "Speeds up SELECT lookups", "Enforces foreign keys", "Compresses text blobs"), 1),
                DiagnosticQuestion(7, "ACID Transactions", "Concurrency Control",
                    "What does the 'A' in ACID database transactions stand for?",
                    listOf("Availability", "Atomicity", "Authorization", "Aggregation"), 1),
                DiagnosticQuestion(8, "Window Functions", "Partitioning & Ordering",
                    "Which keyword defines the window frame for functions like ROW_NUMBER() and RANK()?",
                    listOf("WITHIN", "OVER", "ACROSS", "PARTITION"), 1),
                DiagnosticQuestion(9, "NULL Handling", "Three-Valued Logic",
                    "Which SQL function returns the first non-null expression in a list?",
                    listOf("NVL2", "COALESCE", "ISNULL_ALL", "EMPTY"), 1),
                DiagnosticQuestion(10, "DDL vs DML", "Schema Definition",
                    "Which SQL statement modifies existing records inside a table?",
                    listOf("ALTER", "UPDATE", "MODIFY", "CREATE"), 1)
            )
        }

        if (subjLower.contains("machine learning") || subjLower.contains("ai") || subjLower.contains("data")) {
            return listOf(
                DiagnosticQuestion(1, "Supervised vs Unsupervised", "Data Labeling",
                    "Which learning paradigm trains models using labeled input-output pairs?",
                    listOf("Clustering", "Supervised Learning", "Dimensionality Reduction", "Association Mining"), 1),
                DiagnosticQuestion(2, "Train / Validation / Test Split", "Generalization",
                    "Why is a held-out test set kept completely separate during model training?",
                    listOf("To speed up gradient descent", "To measure unbiased generalization on unseen data", "To increase training epochs", "To normalize features"), 1),
                DiagnosticQuestion(3, "Overfitting & Regularization", "Bias-Variance Tradeoff",
                    "Which symptom indicates a model is overfitting the training dataset?",
                    listOf("High training error and high test error", "Very low training error but high validation error", "Equal loss on all splits", "Zero parameter weights"), 1),
                DiagnosticQuestion(4, "Linear & Logistic Regression", "Loss Functions",
                    "Which task is Logistic Regression primarily used to solve?",
                    listOf("Continuous price forecasting", "Binary or multiclass classification", "Image generation", "Graph clustering"), 1),
                DiagnosticQuestion(5, "Classification Metrics", "Confusion Matrix",
                    "Which metric measures the proportion of predicted positives that are actually positive?",
                    listOf("Recall", "Precision", "Specificity", "MSE"), 1),
                DiagnosticQuestion(6, "Gradient Descent", "Derivatives & Optimization",
                    "What role does the learning rate play in Gradient Descent?",
                    listOf("Sets number of layers", "Controls step size during weight updates", "Selects activation function", "Splits the dataset"), 1),
                DiagnosticQuestion(7, "Feature Scaling", "Input Preprocessing",
                    "Which algorithm is most sensitive to unscaled numerical features?",
                    listOf("Decision Tree", "Random Forest", "K-Nearest Neighbors (KNN)", "Naive Bayes"), 2),
                DiagnosticQuestion(8, "Neural Networks", "Non-Linear Activations",
                    "Why are non-linear activation functions like ReLU needed in neural networks?",
                    listOf("To reduce memory size", "To allow the network to learn non-linear decision boundaries", "To sort input tensors", "To replace backpropagation"), 1),
                DiagnosticQuestion(9, "Edge AI & Quantization", "Model Compression",
                    "How does INT4 quantization help run LLMs on mobile Arm processors?",
                    listOf("Increases parameter count by 4x", "Compresses weight precision to 4 bits, cutting RAM and bandwidth", "Requires cloud GPUs", "Removes tokenization"), 1),
                DiagnosticQuestion(10, "Transformers & Attention", "Sequence Modeling",
                    "What mechanism enables Transformer models to weigh relationships between all tokens in a sequence?",
                    listOf("Max Pooling", "Self-Attention", "K-Means", "Bag-of-Words"), 1)
            )
        }

        // General 10-MCQ bank for Mathematics or any custom selected subject
        return listOf(
            DiagnosticQuestion(1, "Core Terminology & Types", "Foundational Concepts",
                "Which data representation best models a binary true/false state in $subject?",
                listOf("Integer", "Boolean", "Floating-point", "Character string"), 1),
            DiagnosticQuestion(2, "Conditional Logic", "Boolean Algebra",
                "When should a conditional branch be evaluated before executing an operation?",
                listOf("When preconditions or edge cases must be guarded", "Never in structured workflows", "Only after program termination", "Only for constant values"), 0),
            DiagnosticQuestion(3, "Iterative Reasoning", "Loops & Recurrence",
                "When is a condition-controlled loop preferred over a fixed-count loop?",
                listOf("When the exact number of iterations is unknown upfront", "When printing static text", "When declaring constants", "They cannot be interchanged"), 0),
            DiagnosticQuestion(4, "Modular Decomposition", "Abstraction & Reuse",
                "What is the primary engineering benefit of breaking a problem into reusable functions/modules?",
                listOf("Increases duplicate code", "Improves readability, testing, and reuse", "Prevents parameter passing", "Slows down execution"), 1),
            DiagnosticQuestion(5, "Indexed Collections", "Sequential Ordering",
                "What is the index of the first element in a standard 0-indexed sequence?",
                listOf("1", "-1", "0", "Depends on total length"), 2),
            DiagnosticQuestion(6, "Key-Value Mapping", "Associative Lookup",
                "Which structure enables constant-time O(1) average lookup by unique key?",
                listOf("Unsorted Array", "Hash Map / Dictionary", "Singly Linked List", "Stack"), 1),
            DiagnosticQuestion(7, "Algorithmic Efficiency", "Time Complexity",
                "Which time complexity scales fastest as input size n grows very large?",
                listOf("O(n^2)", "O(n)", "O(log n)", "O(n log n)"), 2),
            DiagnosticQuestion(8, "Divide & Conquer", "Recursive Problem Solving",
                "How does Binary Search reduce the search space on each comparison?",
                listOf("Eliminates one element at a time", "Halves the remaining sorted search interval", "Randomly shuffles elements", "Scans from both ends linearly"), 1),
            DiagnosticQuestion(9, "Edge Cases & Validation", "Defensive Design",
                "Which input should always be tested to verify boundary safety?",
                listOf("Empty or zero-length input", "Only average-sized inputs", "Hardcoded outputs", "Ignored warnings"), 0),
            DiagnosticQuestion(10, "Applied Synthesis", "End-to-End Execution",
                "What is the recommended workflow when solving an unfamiliar $subject problem?",
                listOf("Jump straight to optimization without testing", "Clarify inputs/outputs, build a clean baseline, then verify edge cases", "Memorize unrelated formulas", "Skip validation"), 1)
        )
    }

    fun evaluateDiagnostic(
        questions: List<DiagnosticQuestion>,
        selectedAnswers: Map<Int, Int>
    ): KnowledgeGapReport {
        val strong = mutableListOf<String>()
        val weak = mutableListOf<String>()
        val missingPrereqs = mutableListOf<String>()

        questions.forEach { q ->
            val chosen = selectedAnswers[q.id]
            if (chosen == q.correctIndex) {
                strong.add(q.topic)
            } else {
                weak.add(q.topic)
                missingPrereqs.add(q.prerequisite)
            }
        }

        val total = questions.size.coerceAtLeast(1)
        val pct = (strong.size * 100) / total
        val level = when {
            pct >= 80 -> "Intermediate"
            pct >= 50 -> "Developing Beginner"
            else -> "Foundational Beginner"
        }

        return KnowledgeGapReport(
            assignedLevel = level,
            strongTopics = strong,
            weakTopics = weak,
            missingPrerequisites = missingPrereqs.distinct()
        )
    }

    fun generateRoadmap(
        profile: StudentProfile,
        report: KnowledgeGapReport
    ): List<RoadmapModule> {
        val modules = mutableListOf<RoadmapModule>()
        val dailyMins = ((profile.dailyHours.toFloatOrNull() ?: 1.5f) * 60).toInt().coerceIn(30, 180)

        report.weakTopics.forEachIndexed { index, topic ->
            val prereq = report.missingPrerequisites.getOrNull(index) ?: "Core Foundations"
            modules.add(
                RoadmapModule(
                    id = "weak_$index",
                    title = "Master $topic",
                    prerequisite = prereq,
                    dailyTask = "Review $prereq, then solve 3 guided exercises on $topic (${profile.preferredLanguage} notes).",
                    estimatedMinutes = dailyMins
                )
            )
        }

        modules.add(
            RoadmapModule(
                id = "next_1",
                title = "${profile.subject}: Applied Problem Solving",
                prerequisite = report.weakTopics.firstOrNull() ?: "Core Syntax",
                dailyTask = "Build a mini-project combining ${profile.interests} with ${profile.subject}.",
                estimatedMinutes = dailyMins
            )
        )

        modules.add(
            RoadmapModule(
                id = "rev_1",
                title = "Weekly Spaced Revision",
                prerequisite = "Completed Modules",
                dailyTask = "15-minute active recall quiz on weak concepts + mistake review.",
                estimatedMinutes = 30,
                isRevision = true
            )
        )

        return modules
    }

    fun adaptRoadmapForStruggle(
        currentRoadmap: List<RoadmapModule>,
        struggledTopic: String
    ): List<RoadmapModule> {
        val remediation = RoadmapModule(
            id = "adapt_${System.currentTimeMillis()}",
            title = "Adaptive Boost: $struggledTopic",
            prerequisite = "Detected Quiz Struggle",
            dailyTask = "Extra step-by-step breakdown + 5 foundational practice problems on $struggledTopic.",
            estimatedMinutes = 45,
            isRevision = true
        )
        return listOf(remediation) + currentRoadmap
    }
}
