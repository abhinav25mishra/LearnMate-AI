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

data class LearningResourceLink(
    val label: String,
    val url: String
)

data class SubjectTopicNode(
    val id: String,
    val order: Int,
    val title: String,
    val summary: String,
    val flowSteps: List<String>,
    val resources: List<LearningResourceLink>
)

object LearningEngine {

    fun getSubjectTopics(subject: String): List<SubjectTopicNode> {
        val subjLower = subject.lowercase()

        if (subjLower.contains("c programming") || subjLower == "c") {
            return listOf(
                SubjectTopicNode(
                    id = "c_1", order = 1, title = "Variables & Data Types",
                    summary = "Memory allocation, primitive types (int, float, char, double), and format specifiers.",
                    flowSteps = listOf(
                        "1. Prerequisite: Understand binary bits, bytes, and RAM addresses.",
                        "2. Core Syntax: Declare int, float, double, and char variables with printf/scanf specifiers (%d, %f, %c).",
                        "3. Type Modifiers: Explore signed/unsigned, sizeof(), and implicit vs explicit type casting.",
                        "4. Mastery Check: Write a C program calculating byte sizes and formatted outputs without overflow."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: C Data Types", "https://www.geeksforgeeks.org/data-types-in-c/"),
                        LearningResourceLink("Programiz: C Variables & Constants", "https://www.programiz.com/c-programming/c-variables-constants")
                    )
                ),
                SubjectTopicNode(
                    id = "c_2", order = 2, title = "Control Flow & Loops",
                    summary = "Decision making with if-else/switch and iteration using for, while, and do-while.",
                    flowSteps = listOf(
                        "1. Prerequisite: Relational (==, <, >) and logical (&&, ||, !) operators.",
                        "2. Branching: Structure clean if-else ladders and switch-case blocks.",
                        "3. Loops: Trace initialization, condition, and increment across for, while, and do-while loops.",
                        "4. Mastery Check: Build pattern-printing and prime-number programs using nested loops."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Loops in C", "https://www.geeksforgeeks.org/c-loops/"),
                        LearningResourceLink("Programiz: C if...else & Loops", "https://www.programiz.com/c-programming/c-if-else-statement")
                    )
                ),
                SubjectTopicNode(
                    id = "c_3", order = 3, title = "Arrays & Strings",
                    summary = "Contiguous 1D/2D arrays, null-terminated character arrays, and string.h utilities.",
                    flowSteps = listOf(
                        "1. Prerequisite: Variables, 0-based indexing, and for-loop traversal.",
                        "2. 1D & 2D Arrays: Declare fixed-size arrays, traverse elements, and perform matrix operations.",
                        "3. C Strings: Understand '\\0' null termination and functions like strlen(), strcpy(), strcmp().",
                        "4. Mastery Check: Implement linear search, array reversal, and palindrome string checks."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Arrays in C", "https://www.geeksforgeeks.org/c-arrays/"),
                        LearningResourceLink("Programiz: C Strings Guide", "https://www.programiz.com/c-programming/c-strings")
                    )
                ),
                SubjectTopicNode(
                    id = "c_4", order = 4, title = "Functions & Recursion",
                    summary = "Function prototypes, call-by-value, stack frames, and recursive problem solving.",
                    flowSteps = listOf(
                        "1. Prerequisite: Variable scope, return types, and control flow.",
                        "2. Modular Functions: Write function declarations, definitions, and parameter lists.",
                        "3. Call Stack & Recursion: Trace base cases and recursive calls (factorial, Fibonacci, Tower of Hanoi).",
                        "4. Mastery Check: Refactor a monolithic C program into clean, reusable functions."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Functions in C", "https://www.geeksforgeeks.org/c-functions/"),
                        LearningResourceLink("Programiz: C Recursion", "https://www.programiz.com/c-programming/c-recursion")
                    )
                ),
                SubjectTopicNode(
                    id = "c_5", order = 5, title = "Pointers & Dynamic Memory",
                    summary = "Memory addresses (&, *), pointer arithmetic, structs, and malloc/calloc/free.",
                    flowSteps = listOf(
                        "1. Prerequisite: Arrays, functions, and memory layout.",
                        "2. Pointer Basics: Store addresses with *, dereference values, and pass pointers to functions.",
                        "3. Dynamic Heap Allocation: Allocate and free memory safely using malloc(), calloc(), and free().",
                        "4. Mastery Check: Build a singly linked list using struct nodes and dynamic pointers."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Pointers in C", "https://www.geeksforgeeks.org/c-pointers/"),
                        LearningResourceLink("Programiz: C Dynamic Memory", "https://www.programiz.com/c-programming/c-dynamic-memory-allocation")
                    )
                )
            )
        }

        if (subjLower.contains("data structure") || subjLower.contains("dsa")) {
            return listOf(
                SubjectTopicNode(
                    id = "dsa_1", order = 1, title = "Time & Space Complexity (Big-O)",
                    summary = "Asymptotic analysis (O(1), O(log n), O(n), O(n log n), O(n^2)) and recurrence relations.",
                    flowSteps = listOf(
                        "1. Prerequisite: Basic algebra, logarithms, and loop counting.",
                        "2. Growth Rates: Compare constant, logarithmic, linear, and quadratic runtime curves.",
                        "3. Space Analysis: Measure auxiliary memory and recursion call-stack depth.",
                        "4. Mastery Check: Analyze best, average, and worst-case complexities of nested loops."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Asymptotic Analysis", "https://www.geeksforgeeks.org/analysis-of-algorithms-set-1-asymptotic-analysis/"),
                        LearningResourceLink("Big-O Cheat Sheet", "https://www.bigocheatsheet.com/")
                    )
                ),
                SubjectTopicNode(
                    id = "dsa_2", order = 2, title = "Arrays & Linked Lists",
                    summary = "Contiguous memory indexing vs pointer-based singly, doubly, and circular linked lists.",
                    flowSteps = listOf(
                        "1. Prerequisite: Pointers/references, dynamic memory, and loops.",
                        "2. Array Techniques: Master two-pointers, sliding window, and prefix sums.",
                        "3. Linked List Operations: Implement insertion, deletion, and traversal in singly & circular lists.",
                        "4. Mastery Check: Reverse a linked list in-place and detect cycles in O(n) time."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Linked List Data Structure", "https://www.geeksforgeeks.org/data-structures/linked-list/"),
                        LearningResourceLink("VisuAlgo: Linked List Visualizer", "https://visualgo.net/en/list")
                    )
                ),
                SubjectTopicNode(
                    id = "dsa_3", order = 3, title = "Stacks & Queues",
                    summary = "LIFO and FIFO abstract data types, circular queues, and expression evaluation.",
                    flowSteps = listOf(
                        "1. Prerequisite: Arrays and singly linked lists.",
                        "2. Stack Mechanics: Implement push, pop, peek, and balanced parenthesis checking.",
                        "3. Queue Mechanics: Build linear queues, circular queues, and deques.",
                        "4. Mastery Check: Convert infix expressions to postfix and evaluate using a stack."
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Stack Data Structure", "https://www.geeksforgeeks.org/stack-data-structure/"),
                        LearningResourceLink("GeeksforGeeks: Queue Data Structure", "https://www.geeksforgeeks.org/queue-data-structure/")
                    )
                ),
                SubjectTopicNode(
                    id = "dsa_4", order = 4, title = "Trees, BST & AVL Trees",
                    summary = "Hierarchical nodes, traversals (preorder/inorder/postorder), BST search, and AVL rotations.",
                    flowSteps = listOf(
                        "1. Prerequisite: Recursion, queues (for level-order BFS), and pointers.",
                        "2. Tree Traversals: Master recursive and iterative Preorder, Inorder, and Postorder traversals.",
                        "3. Binary Search Trees (BST): Implement O(log n) search, insertion, and deletion.",
                        "4. Mastery Check: Balance skewed trees using LL, RR, LR, and RL rotations in AVL trees."
                    ),
                    resources = listOf(
                        LearningResourceLink("VisuAlgo: BST & AVL Visualizer", "https://visualgo.net/en/bst"),
                        LearningResourceLink("GeeksforGeeks: AVL Tree Insertion", "https://www.geeksforgeeks.org/insertion-in-an-avl-tree/")
                    )
                ),
                SubjectTopicNode(
                    id = "dsa_5", order = 5, title = "Sorting & Searching Algorithms",
                    summary = "Binary search, Quick Sort, Merge Sort, Heap Sort, and divide-and-conquer.",
                    flowSteps = listOf(
                        "1. Prerequisite: Arrays, recursion, and Big-O complexity.",
                        "2. Binary Search: Master iterative and recursive O(log n) search on sorted collections.",
                        "3. Divide & Conquer Sorts: Trace partitioning in Quick Sort and merging in Merge Sort.",
                        "4. Mastery Check: Compare stability, in-place memory, and worst-case runtimes across sorting algorithms."
                    ),
                    resources = listOf(
                        LearningResourceLink("VisuAlgo: Sorting Visualizer", "https://visualgo.net/en/sorting"),
                        LearningResourceLink("GeeksforGeeks: QuickSort Algorithm", "https://www.geeksforgeeks.org/quick-sort/")
                    )
                )
            )
        }

        // Default & Python Curriculum (Variables -> Data Types -> Arrays/Lists -> Strings -> Functions)
        return listOf(
            SubjectTopicNode(
                id = "py_1", order = 1, title = "Variables & Expressions",
                summary = "Dynamic typing, naming conventions, assignment operators, and input/output.",
                flowSteps = listOf(
                    "1. Prerequisite: Basic arithmetic and understanding how programs store values in memory.",
                    "2. Variable Assignment: Create variables with descriptive snake_case names and inspect types with type().",
                    "3. User Input & Formatting: Read input with input(), cast types, and format output with f-strings.",
                    "4. Mastery Check: Build an interactive calculator or unit converter with clean input validation."
                ),
                resources = listOf(
                    LearningResourceLink("Official Python Tutorial: Introduction", "https://docs.python.org/3/tutorial/introduction.html"),
                    LearningResourceLink("Real Python: Variables in Python", "https://realpython.com/python-variables/")
                )
            ),
            SubjectTopicNode(
                id = "py_2", order = 2, title = "Data Types & Control Flow",
                summary = "int, float, bool, None, conditional branching (if/elif/else), and loops (for/while).",
                flowSteps = listOf(
                    "1. Prerequisite: Variables and comparison operators (==, !=, <, >).",
                    "2. Core Data Types: Work with integers, floats, booleans, and truthy/falsy evaluations.",
                    "3. Branching & Loops: Control execution with if-elif-else, range(), for loops, and while loops.",
                    "4. Mastery Check: Write a number-guessing or prime-factorization script using loops and break/continue."
                ),
                resources = listOf(
                    LearningResourceLink("Official Python Docs: Control Flow", "https://docs.python.org/3/tutorial/controlflow.html"),
                    LearningResourceLink("W3Schools: Python Data Types", "https://www.w3schools.com/python/python_datatypes.asp")
                )
            ),
            SubjectTopicNode(
                id = "py_3", order = 3, title = "Arrays, Lists & Dictionaries",
                summary = "Dynamic lists, slicing, list comprehensions, tuples, sets, and O(1) dictionaries.",
                flowSteps = listOf(
                    "1. Prerequisite: Loops, indexing, and core data types.",
                    "2. Lists & Slicing: Master append(), pop(), slicing [start:stop:step], and list comprehensions.",
                    "3. Key-Value & Set Structures: Use dict.get() and sets for fast membership checks and deduplication.",
                    "4. Mastery Check: Solve frequency-counting and Two-Sum problems using dictionaries in O(n) time."
                ),
                resources = listOf(
                    LearningResourceLink("Official Python Docs: Data Structures", "https://docs.python.org/3/tutorial/datastructures.html"),
                    LearningResourceLink("Real Python: Lists and Tuples", "https://realpython.com/python-lists-tuples/")
                )
            ),
            SubjectTopicNode(
                id = "py_4", order = 4, title = "Strings & Text Processing",
                summary = "Immutable strings, slicing, split/join, formatting, and pattern manipulation.",
                flowSteps = listOf(
                    "1. Prerequisite: 0-based indexing, slicing, and loops.",
                    "2. Core String Methods: Master strip(), split(), join(), replace(), find(), and case transforms.",
                    "3. String Formatting: Build clean templates with f-strings and multi-line strings.",
                    "4. Mastery Check: Write an anagram checker and word-frequency analyzer."
                ),
                resources = listOf(
                    LearningResourceLink("Real Python: Strings and Character Data", "https://realpython.com/python-strings/"),
                    LearningResourceLink("W3Schools: Python String Methods", "https://www.w3schools.com/python/python_strings_methods.asp")
                )
            ),
            SubjectTopicNode(
                id = "py_5", order = 5, title = "Functions & Modular Code",
                summary = "Defining functions with def, parameters, default/keyword arguments, scope, and recursion.",
                flowSteps = listOf(
                    "1. Prerequisite: Variables, data types, control flow, and lists/strings.",
                    "2. Function Anatomy: Define clean functions with def, parameters, docstrings, and return values.",
                    "3. Arguments & Scope: Master default arguments, *args/**kwargs, LEGB variable scope, and lambda expressions.",
                    "4. Mastery Check: Decompose a multi-step problem into small, testable functions with recursion and edge-case handling."
                ),
                resources = listOf(
                    LearningResourceLink("Official Python Docs: Defining Functions", "https://docs.python.org/3/tutorial/controlflow.html#defining-functions"),
                    LearningResourceLink("Real Python: Defining Your Own Python Function", "https://realpython.com/defining-your-own-python-function/")
                )
            )
        )
    }

    fun getDiagnosticQuestions(subject: String): List<DiagnosticQuestion> {
        val subjLower = subject.lowercase()

        if (subjLower.contains("c programming") || subjLower == "c") {
            return listOf(
                DiagnosticQuestion(1, "Data Types & Format Specifiers", "Variables & Memory",
                    "Which format specifier is used in printf() to print a float value in C?",
                    listOf("%d", "%c", "%f", "%s"), 2),
                DiagnosticQuestion(2, "Control Flow & Loops", "Boolean Logic",
                    "Which loop in C is guaranteed to execute its body at least once?",
                    listOf("for loop", "while loop", "do-while loop", "foreach loop"), 2),
                DiagnosticQuestion(3, "Arrays & Indexing", "Contiguous Memory",
                    "What is the valid index range for an array declared as int arr[10]; in C?",
                    listOf("1 to 10", "0 to 9", "0 to 10", "-1 to 9"), 1),
                DiagnosticQuestion(4, "C Strings", "Character Arrays",
                    "Which character marks the end of every standard string in C?",
                    listOf("'\\n'", "'\\0'", "'\\t'", "EOF"), 1),
                DiagnosticQuestion(5, "Functions & Prototypes", "Modular Code",
                    "What return type indicates that a C function does not return any value?",
                    listOf("null", "none", "void", "empty"), 2),
                DiagnosticQuestion(6, "Pointers Basics", "Memory Addresses",
                    "Which operator retrieves the memory address of a variable in C?",
                    listOf("*", "&", "->", "%"), 1),
                DiagnosticQuestion(7, "Dynamic Memory Allocation", "Heap vs Stack",
                    "Which header file declares malloc(), calloc(), and free() in C?",
                    listOf("<stdio.h>", "<string.h>", "<stdlib.h>", "<math.h>"), 2),
                DiagnosticQuestion(8, "Structures", "User-Defined Types",
                    "Which operator accesses a struct member through a pointer to that struct?",
                    listOf(".", "::", "->", "&"), 2),
                DiagnosticQuestion(9, "Operators & Precedence", "Expressions",
                    "What is the value of 7 / 2 in integer division in C?",
                    listOf("3.5", "3", "4", "1"), 1),
                DiagnosticQuestion(10, "Recursion & Call Stack", "Functions",
                    "What prevents infinite recursion in a recursive C function?",
                    listOf("A global counter", "A base case condition", "A break statement", "A header guard"), 1)
            )
        }

        if (subjLower.contains("data structure") || subjLower.contains("dsa")) {
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

        if (subjLower.contains("sql") || subjLower.contains("database")) {
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

        // Python & General 10-MCQ Bank
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
        val dailyMins = ((profile.dailyHours.toFloatOrNull() ?: 1.5f) * 60).toInt().coerceIn(30, 180)
        return getSubjectTopics(profile.subject).map { node ->
            RoadmapModule(
                id = node.id,
                title = node.title,
                prerequisite = node.flowSteps.firstOrNull()?.removePrefix("1. Prerequisite: ") ?: "Core Foundations",
                dailyTask = node.summary,
                estimatedMinutes = dailyMins
            )
        }
    }

    fun adaptRoadmapForStruggle(
        currentRoadmap: List<RoadmapModule>,
        struggledTopic: String
    ): List<RoadmapModule> {
        val remediation = RoadmapModule(
            id = "adapt_${System.currentTimeMillis()}",
            title = "Practice Boost: $struggledTopic",
            prerequisite = "Foundational Review",
            dailyTask = "Extra step-by-step walkthrough + 5 guided exercises on $struggledTopic.",
            estimatedMinutes = 45,
            isRevision = true
        )
        return listOf(remediation) + currentRoadmap
    }
}
