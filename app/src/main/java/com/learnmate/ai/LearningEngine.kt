package com.learnmate.ai

import kotlin.random.Random

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

    /**
     * Deterministic per-question shuffle across A, B, C, D so the correct answer
     * is evenly jumbled across options A (0), B (1), C (2), and D (3) while
     * preserving accurate evaluation and answer key display.
     */
    private fun makeShuffledQuestion(
        id: Int,
        topic: String,
        prerequisite: String,
        question: String,
        options: List<String>,
        correctIndex: Int
    ): DiagnosticQuestion {
        val correctText = options.getOrElse(correctIndex) { options.first() }
        val rng = Random(question.hashCode() + id * 31)
        val shuffled = options.shuffled(rng)
        val newCorrectIdx = shuffled.indexOf(correctText).coerceAtLeast(0)
        return DiagnosticQuestion(
            id = id,
            topic = topic,
            prerequisite = prerequisite,
            question = question,
            options = shuffled,
            correctIndex = newCorrectIdx
        )
    }

    fun getSubjectTopics(subject: String): List<SubjectTopicNode> {
        val subjLower = subject.trim().lowercase()

        if (subjLower.contains("physic")) {
            return listOf(
                SubjectTopicNode(
                    id = "phy_1", order = 1, title = "Units, Dimensions & Vectors",
                    summary = "SI units, dimensional analysis, scalar vs vector quantities, and vector resolution.",
                    flowSteps = listOf(
                        "Stage 1: Fundamental & Derived SI Units + Dimensional Formulas",
                        "Stage 2: Significant Figures, Measurement Errors & Vernier/Screw Gauge",
                        "Stage 3: Vector Addition (Triangle & Parallelogram Laws) and Unit Vectors (i, j, k)",
                        "Stage 4: Dot Product & Cross Product Applications in Mechanics"
                    ),
                    resources = listOf(
                        LearningResourceLink("Khan Academy: Vectors & Kinematics Intro", "https://www.khanacademy.org/science/physics/one-dimensional-motion"),
                        LearningResourceLink("Physics Classroom: Vectors & Motion", "https://www.physicsclassroom.com/class/vectors")
                    )
                ),
                SubjectTopicNode(
                    id = "phy_2", order = 2, title = "Kinematics & Newton's Laws of Motion",
                    summary = "1D/2D projectile motion, free-body diagrams, friction, and circular dynamics.",
                    flowSteps = listOf(
                        "Stage 1: Displacement, Velocity, Acceleration & Equations of Uniformly Accelerated Motion",
                        "Stage 2: Projectile Motion on Horizontal & Inclined Planes",
                        "Stage 3: Newton's First, Second (F = ma) & Third Laws with Free-Body Diagrams",
                        "Stage 4: Static/Kinetic Friction & Centripetal Force in Circular Motion"
                    ),
                    resources = listOf(
                        LearningResourceLink("Khan Academy: Forces & Newton's Laws", "https://www.khanacademy.org/science/physics/forces-newtons-laws"),
                        LearningResourceLink("HyperPhysics: Mechanics & Motion", "http://hyperphysics.phy-astr.gsu.edu/hbase/hframe.html")
                    )
                ),
                SubjectTopicNode(
                    id = "phy_3", order = 3, title = "Work, Energy, Power & Rotational Mechanics",
                    summary = "Work-energy theorem, conservation of mechanical energy, momentum, and torque.",
                    flowSteps = listOf(
                        "Stage 1: Work Done by Constant & Variable Forces + Kinetic & Potential Energy",
                        "Stage 2: Law of Conservation of Mechanical Energy & Power",
                        "Stage 3: Center of Mass, Linear Momentum & Elastic/Inelastic Collisions",
                        "Stage 4: Moment of Inertia, Torque (τ = Iα) & Conservation of Angular Momentum"
                    ),
                    resources = listOf(
                        LearningResourceLink("Khan Academy: Work & Energy", "https://www.khanacademy.org/science/physics/work-and-energy"),
                        LearningResourceLink("Physics Classroom: Work, Energy & Power", "https://www.physicsclassroom.com/class/energy")
                    )
                ),
                SubjectTopicNode(
                    id = "phy_4", order = 4, title = "Electromagnetism & Optics",
                    summary = "Coulomb's law, Ohm's law circuits, magnetic fields, electromagnetic induction, and lenses.",
                    flowSteps = listOf(
                        "Stage 1: Electrostatics (Coulomb's Law, Electric Field & Gauss's Law)",
                        "Stage 2: Current Electricity (Ohm's Law, Series/Parallel Resistors & Kirchhoff's Rules)",
                        "Stage 3: Magnetic Effects of Current, Lorentz Force & Faraday's Law of Induction",
                        "Stage 4: Ray Optics (Reflection, Snell's Law Refraction, Thin Lenses & Wave Interference)"
                    ),
                    resources = listOf(
                        LearningResourceLink("Khan Academy: Electricity & Magnetism", "https://www.khanacademy.org/science/physics/circuits-topic"),
                        LearningResourceLink("Physics Classroom: Light Waves & Optics", "https://www.physicsclassroom.com/class/refrn")
                    )
                )
            )
        }

        if (subjLower.contains("math")) {
            return listOf(
                SubjectTopicNode(
                    id = "math_1", order = 1, title = "Number Systems, Algebra & Polynomials",
                    summary = "Real/complex numbers, quadratic equations, sequences, series, and logarithms.",
                    flowSteps = listOf(
                        "Stage 1: Number Systems, Exponents, Surds & Logarithmic Identities",
                        "Stage 2: Linear & Quadratic Equations (Roots, Discriminant & Vieta's Formulas)",
                        "Stage 3: Arithmetic & Geometric Progressions (AP, GP & Binomial Theorem)",
                        "Stage 4: Permutations, Combinations & Mathematical Induction"
                    ),
                    resources = listOf(
                        LearningResourceLink("Khan Academy: Algebra & Polynomials", "https://www.khanacademy.org/math/algebra"),
                        LearningResourceLink("Paul's Online Math Notes: Algebra", "https://tutorial.math.lamar.edu/Classes/Alg/Alg.aspx")
                    )
                ),
                SubjectTopicNode(
                    id = "math_2", order = 2, title = "Trigonometry & Coordinate Geometry",
                    summary = "Trigonometric identities, inverse functions, straight lines, circles, and conic sections.",
                    flowSteps = listOf(
                        "Stage 1: Radian Measure, Unit Circle & Fundamental Trigonometric Identities",
                        "Stage 2: Compound Angles, Multiple Angles & Trigonometric Equations",
                        "Stage 3: Cartesian Plane, Slope, Straight Lines & Pair of Lines",
                        "Stage 4: Circles, Parabola, Ellipse & Hyperbola Standard Equations"
                    ),
                    resources = listOf(
                        LearningResourceLink("Khan Academy: Trigonometry", "https://www.khanacademy.org/math/trigonometry"),
                        LearningResourceLink("GeeksforGeeks: Coordinate Geometry Notes", "https://www.geeksforgeeks.org/coordinate-geometry/")
                    )
                ),
                SubjectTopicNode(
                    id = "math_3", order = 3, title = "Differential & Integral Calculus",
                    summary = "Limits, continuity, derivatives, maxima/minima optimization, and definite integrals.",
                    flowSteps = listOf(
                        "Stage 1: Functions, Domain/Range, Limits & Continuity",
                        "Stage 2: Differentiation Rules (Product, Quotient, Chain Rule & Implicit Derivatives)",
                        "Stage 3: Applications of Derivatives (Tangents, Rates of Change & Maxima/Minima)",
                        "Stage 4: Indefinite & Definite Integrals, Area Under Curves & Differential Equations"
                    ),
                    resources = listOf(
                        LearningResourceLink("Paul's Online Math Notes: Calculus I", "https://tutorial.math.lamar.edu/Classes/CalcI/CalcI.aspx"),
                        LearningResourceLink("Khan Academy: Differential & Integral Calculus", "https://www.khanacademy.org/math/calculus-1")
                    )
                ),
                SubjectTopicNode(
                    id = "math_4", order = 4, title = "Matrices, Vectors & Probability",
                    summary = "Matrix operations, determinants, 3D vectors, Bayes' theorem, and distributions.",
                    flowSteps = listOf(
                        "Stage 1: Matrix Algebra, Transpose, Determinants & Matrix Inverse",
                        "Stage 2: Solving Systems of Linear Equations & Eigenvalues",
                        "Stage 3: Conditional Probability, Bayes' Theorem & Random Variables",
                        "Stage 4: Binomial, Poisson & Normal Statistical Distributions"
                    ),
                    resources = listOf(
                        LearningResourceLink("Khan Academy: Linear Algebra", "https://www.khanacademy.org/math/linear-algebra"),
                        LearningResourceLink("Khan Academy: Statistics & Probability", "https://www.khanacademy.org/math/statistics-probability")
                    )
                )
            )
        }

        if (subjLower.contains("c programming") || subjLower == "c") {
            return listOf(
                SubjectTopicNode(
                    id = "c_1", order = 1, title = "Variables, Data Types & Operators",
                    summary = "Primitive types (int, float, char, double), format specifiers, and operator precedence.",
                    flowSteps = listOf(
                        "Stage 1: C Program Structure (#include <stdio.h>, main(), compilation workflow)",
                        "Stage 2: Primitive Data Types (int, float, double, char) & sizeof() Memory Sizes",
                        "Stage 3: Formatted Input/Output using printf() & scanf() (%d, %f, %c)",
                        "Stage 4: Arithmetic, Relational, Logical & Bitwise Operators with Type Casting"
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: C Data Types", "https://www.geeksforgeeks.org/data-types-in-c/"),
                        LearningResourceLink("Programiz: C Variables & Constants", "https://www.programiz.com/c-programming/c-variables-constants")
                    )
                ),
                SubjectTopicNode(
                    id = "c_2", order = 2, title = "Control Flow & Loops",
                    summary = "Conditional branching (if-else, switch) and iteration (for, while, do-while).",
                    flowSteps = listOf(
                        "Stage 1: if, if-else, Nested if-else & Ternary Operator (? :)",
                        "Stage 2: Multi-Way Branching with switch-case & break",
                        "Stage 3: Entry-Controlled Loops (for, while) vs Exit-Controlled Loop (do-while)",
                        "Stage 4: Nested Loops, Pattern Generation & Loop Control (break, continue)"
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Loops in C", "https://www.geeksforgeeks.org/c-loops/"),
                        LearningResourceLink("Programiz: C if...else & Loops", "https://www.programiz.com/c-programming/c-if-else-statement")
                    )
                ),
                SubjectTopicNode(
                    id = "c_3", order = 3, title = "Arrays & C Strings",
                    summary = "1D/2D arrays, matrix operations, null-terminated char arrays, and <string.h>.",
                    flowSteps = listOf(
                        "Stage 1: 1D Array Declaration, Memory Layout & Traversal",
                        "Stage 2: Searching & Sorting on 1D Arrays + 2D Matrix Addition/Multiplication",
                        "Stage 3: Null-Terminated Character Arrays ('\\0') & Safe String Input",
                        "Stage 4: Standard <string.h> Functions (strlen, strcpy, strcat, strcmp)"
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Arrays in C", "https://www.geeksforgeeks.org/c-arrays/"),
                        LearningResourceLink("Programiz: C Strings Guide", "https://www.programiz.com/c-programming/c-strings")
                    )
                ),
                SubjectTopicNode(
                    id = "c_4", order = 4, title = "Functions, Recursion & Pointers",
                    summary = "Function prototypes, call-by-value vs call-by-reference, recursion, and dynamic memory.",
                    flowSteps = listOf(
                        "Stage 1: Function Declaration, Definition, Arguments & Return Types",
                        "Stage 2: Storage Classes (auto, static, extern) & Recursive Call Stack",
                        "Stage 3: Pointers (& address-of, * dereference), Pointer Arithmetic & Arrays",
                        "Stage 4: Structures (struct) & Heap Memory Management (malloc, calloc, free)"
                    ),
                    resources = listOf(
                        LearningResourceLink("GeeksforGeeks: Functions in C", "https://www.geeksforgeeks.org/c-functions/"),
                        LearningResourceLink("GeeksforGeeks: Pointers in C", "https://www.geeksforgeeks.org/c-pointers/")
                    )
                )
            )
        }

        if (subjLower.contains("sql") || subjLower.contains("database") || subjLower.contains("dbms")) {
            return listOf(
                SubjectTopicNode(
                    id = "sql_1", order = 1, title = "Database Schema, DDL & Constraints",
                    summary = "Relational tables, data types, CREATE/ALTER/DROP, and Primary/Foreign keys.",
                    flowSteps = listOf(
                        "Stage 1: Relational Model (Tables, Tuples, Attributes & Candidate Keys)",
                        "Stage 2: DDL Commands (CREATE TABLE, ALTER TABLE, DROP, TRUNCATE)",
                        "Stage 3: Integrity Constraints (PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, CHECK)",
                        "Stage 4: DML Operations (INSERT, UPDATE, DELETE) with Safe WHERE Clauses"
                    ),
                    resources = listOf(
                        LearningResourceLink("PostgreSQL Official Tutorial: SQL Language", "https://www.postgresql.org/docs/current/tutorial-sql.html"),
                        LearningResourceLink("W3Schools: SQL CREATE TABLE & Constraints", "https://www.w3schools.com/sql/sql_create_table.asp")
                    )
                ),
                SubjectTopicNode(
                    id = "sql_2", order = 2, title = "SELECT Queries, Filtering & Sorting",
                    summary = "SELECT, DISTINCT, WHERE operators (IN, BETWEEN, LIKE), ORDER BY, and LIMIT.",
                    flowSteps = listOf(
                        "Stage 1: Column Projection (SELECT, AS Aliases, DISTINCT)",
                        "Stage 2: Row Filtering with WHERE (=, <>, IN, BETWEEN, IS NULL)",
                        "Stage 3: Pattern Matching with LIKE (% and _ wildcards)",
                        "Stage 4: Multi-Column Sorting (ORDER BY ASC/DESC) & Pagination (LIMIT/OFFSET)"
                    ),
                    resources = listOf(
                        LearningResourceLink("SQLBolt: Interactive SQL Lessons 1–5", "https://sqlbolt.com/"),
                        LearningResourceLink("GeeksforGeeks: SQL SELECT & WHERE Clause", "https://www.geeksforgeeks.org/sql-select-query/")
                    )
                ),
                SubjectTopicNode(
                    id = "sql_3", order = 3, title = "SQL Joins, Subqueries & CTEs",
                    summary = "INNER, LEFT, RIGHT, FULL OUTER, SELF joins, nested subqueries, and WITH CTEs.",
                    flowSteps = listOf(
                        "Stage 1: INNER JOIN Across Foreign-Key Relationships",
                        "Stage 2: LEFT JOIN, RIGHT JOIN & Finding Unmatched Records (IS NULL)",
                        "Stage 3: SELF JOIN & Set Operations (UNION, UNION ALL, INTERSECT)",
                        "Stage 4: Scalar/Correlated Subqueries & Common Table Expressions (WITH)"
                    ),
                    resources = listOf(
                        LearningResourceLink("SQLBolt: Multi-Table Queries with JOINs", "https://sqlbolt.com/lesson/select_queries_with_joins"),
                        LearningResourceLink("GeeksforGeeks: SQL Join Types Explained", "https://www.geeksforgeeks.org/sql-join-set-1-inner-left-right-and-full-joins/")
                    )
                ),
                SubjectTopicNode(
                    id = "sql_4", order = 4, title = "Aggregations, Window Functions & Normalization",
                    summary = "GROUP BY, HAVING, OVER (PARTITION BY), 1NF–3NF normalization, and indexing.",
                    flowSteps = listOf(
                        "Stage 1: Aggregate Functions (COUNT, SUM, AVG, MIN, MAX) & GROUP BY",
                        "Stage 2: Filtering Grouped Results with HAVING vs Pre-Filter WHERE",
                        "Stage 3: Window Functions (ROW_NUMBER, RANK, DENSE_RANK, LAG, LEAD)",
                        "Stage 4: Normalization (1NF, 2NF, 3NF), B-Tree Indexes & ACID Transactions"
                    ),
                    resources = listOf(
                        LearningResourceLink("PostgreSQL Docs: Window Functions", "https://www.postgresql.org/docs/current/tutorial-window.html"),
                        LearningResourceLink("GeeksforGeeks: Database Normalization (1NF–3NF)", "https://www.geeksforgeeks.org/normal-forms-in-dbms/")
                    )
                )
            )
        }

        if (subjLower.contains("data structure") || subjLower.contains("dsa")) {
            return listOf(
                SubjectTopicNode(
                    id = "dsa_1", order = 1, title = "Asymptotic Complexity & Arrays",
                    summary = "Big-O notation, two-pointer techniques, sliding window, and binary search.",
                    flowSteps = listOf(
                        "Stage 1: Time & Space Complexity (O(1), O(log n), O(n), O(n log n), O(n^2))",
                        "Stage 2: 1D/2D Arrays, Prefix Sums & Subarray Kadane's Algorithm",
                        "Stage 3: Two-Pointer & Sliding Window Patterns",
                        "Stage 4: Binary Search on Sorted Arrays & Search Space"
                    ),
                    resources = listOf(
                        LearningResourceLink("Big-O Complexity Cheat Sheet", "https://www.bigocheatsheet.com/"),
                        LearningResourceLink("GeeksforGeeks: Array Data Structure", "https://www.geeksforgeeks.org/array-data-structure/")
                    )
                ),
                SubjectTopicNode(
                    id = "dsa_2", order = 2, title = "Linked Lists, Stacks & Queues",
                    summary = "Singly/doubly/circular linked lists, LIFO stacks, and FIFO circular queues.",
                    flowSteps = listOf(
                        "Stage 1: Singly, Doubly & Circular Linked List Insertion, Deletion & Reversal",
                        "Stage 2: Fast & Slow Pointers (Cycle Detection & Middle Node)",
                        "Stage 3: Stack Operations, Balanced Parentheses & Infix-to-Postfix",
                        "Stage 4: Linear Queues, Circular Queues & Monotonic Deques"
                    ),
                    resources = listOf(
                        LearningResourceLink("VisuAlgo: Linked List, Stack & Queue Visualizer", "https://visualgo.net/en/list"),
                        LearningResourceLink("GeeksforGeeks: Linked List Tutorials", "https://www.geeksforgeeks.org/data-structures/linked-list/")
                    )
                ),
                SubjectTopicNode(
                    id = "dsa_3", order = 3, title = "Trees, BST & AVL Balanced Trees",
                    summary = "Tree traversals, Binary Search Trees, AVL rotations, and Heaps.",
                    flowSteps = listOf(
                        "Stage 1: Binary Tree Terminology (Root, Height, Depth, Degree) & DFS/BFS Traversals",
                        "Stage 2: Binary Search Tree (BST) Search, Insertion & Deletion",
                        "Stage 3: AVL Tree Balance Factors & LL, RR, LR, RL Rotations",
                        "Stage 4: Binary Min/Max Heaps, Heapify & Priority Queues"
                    ),
                    resources = listOf(
                        LearningResourceLink("VisuAlgo: BST & AVL Tree Visualizer", "https://visualgo.net/en/bst"),
                        LearningResourceLink("GeeksforGeeks: AVL Tree Rotations", "https://www.geeksforgeeks.org/insertion-in-an-avl-tree/")
                    )
                ),
                SubjectTopicNode(
                    id = "dsa_4", order = 4, title = "Sorting, Hashing & Graphs",
                    summary = "Quick Sort, Merge Sort, hash tables, BFS/DFS graph traversals, and shortest paths.",
                    flowSteps = listOf(
                        "Stage 1: Divide & Conquer Sorting (Merge Sort, Quick Sort & Partitioning)",
                        "Stage 2: Hash Tables, Hash Functions & Collision Resolution (Chaining & Probing)",
                        "Stage 3: Graph Representations (Adjacency List/Matrix) & BFS/DFS Traversals",
                        "Stage 4: Topological Sort & Dijkstra's Shortest Path Algorithm"
                    ),
                    resources = listOf(
                        LearningResourceLink("VisuAlgo: Sorting Algorithms Visualizer", "https://visualgo.net/en/sorting"),
                        LearningResourceLink("GeeksforGeeks: Graph Data Structure & Algorithms", "https://www.geeksforgeeks.org/graph-data-structure-and-algorithms/")
                    )
                )
            )
        }

        if (subjLower.contains("python")) {
            return listOf(
                SubjectTopicNode(
                    id = "py_1", order = 1, title = "Variables & Core Data Types",
                    summary = "Dynamic typing, int, float, bool, None, type casting, and f-string formatting.",
                    flowSteps = listOf(
                        "Stage 1: Python Syntax, Indentation, Variables & Naming Rules (snake_case)",
                        "Stage 2: Primitive Data Types (int, float, bool, None) & type() / isinstance()",
                        "Stage 3: User Input (input()), Type Conversion & Formatted Output (f-strings)",
                        "Stage 4: Arithmetic, Comparison, Logical & Membership Operators"
                    ),
                    resources = listOf(
                        LearningResourceLink("Official Python Tutorial: Introduction", "https://docs.python.org/3/tutorial/introduction.html"),
                        LearningResourceLink("Real Python: Variables in Python", "https://realpython.com/python-variables/")
                    )
                ),
                SubjectTopicNode(
                    id = "py_2", order = 2, title = "Control Flow & Loops",
                    summary = "if-elif-else branching, range(), for loops, while loops, and loop control.",
                    flowSteps = listOf(
                        "Stage 1: Conditional Branching with if, elif, else & Truthy/Falsy Values",
                        "Stage 2: Iterating Sequences with for Loops & range(start, stop, step)",
                        "Stage 3: Condition-Controlled while Loops & Sentinel Patterns",
                        "Stage 4: Nested Loops, enumerate(), zip(), break & continue"
                    ),
                    resources = listOf(
                        LearningResourceLink("Official Python Docs: Control Flow", "https://docs.python.org/3/tutorial/controlflow.html"),
                        LearningResourceLink("Real Python: Conditional Statements & Loops", "https://realpython.com/python-conditional-statements/")
                    )
                ),
                SubjectTopicNode(
                    id = "py_3", order = 3, title = "Lists, Tuples, Sets & Dictionaries",
                    summary = "Mutable lists, slicing, list comprehensions, tuples, sets, and O(1) hash dictionaries.",
                    flowSteps = listOf(
                        "Stage 1: Lists, Indexing, Slicing [start:stop:step] & In-Place Methods",
                        "Stage 2: List Comprehensions & Immutable Tuples",
                        "Stage 3: Dictionaries (Key-Value Mapping, .get(), .items()) & Hash Sets",
                        "Stage 4: Nested Data Structures & Time-Complexity Tradeoffs"
                    ),
                    resources = listOf(
                        LearningResourceLink("Official Python Docs: Data Structures", "https://docs.python.org/3/tutorial/datastructures.html"),
                        LearningResourceLink("Real Python: Lists, Tuples & Dictionaries", "https://realpython.com/python-lists-tuples/")
                    )
                ),
                SubjectTopicNode(
                    id = "py_4", order = 4, title = "Strings & File Handling",
                    summary = "Immutable strings, split/join, string methods, and context managers (with open).",
                    flowSteps = listOf(
                        "Stage 1: String Indexing, Slicing & Immutability",
                        "Stage 2: String Methods (strip, split, join, replace, find, startswith)",
                        "Stage 3: Reading & Writing Text/CSV/JSON Files safely using with open()",
                        "Stage 4: Exception Handling with try / except / else / finally"
                    ),
                    resources = listOf(
                        LearningResourceLink("Real Python: Strings and Character Data", "https://realpython.com/python-strings/"),
                        LearningResourceLink("Real Python: Reading & Writing Files in Python", "https://realpython.com/read-write-files-python/")
                    )
                ),
                SubjectTopicNode(
                    id = "py_5", order = 5, title = "Functions, Scope & OOP",
                    summary = "def functions, *args/**kwargs, LEGB scope, recursion, and classes/objects.",
                    flowSteps = listOf(
                        "Stage 1: Defining Functions with def, Parameters, Docstrings & return",
                        "Stage 2: Default Arguments, Keyword Arguments, *args & **kwargs",
                        "Stage 3: LEGB Variable Scope, Lambda Functions & Recursion",
                        "Stage 4: Classes, __init__, Instance Methods, Inheritance & Modules"
                    ),
                    resources = listOf(
                        LearningResourceLink("Official Python Docs: Defining Functions", "https://docs.python.org/3/tutorial/controlflow.html#defining-functions"),
                        LearningResourceLink("Real Python: Object-Oriented Programming in Python", "https://realpython.com/python3-object-oriented-programming/")
                    )
                )
            )
        }

        // Dynamic, subject-specific curriculum for ANY custom searched/selected topic (e.g., History, Geography, Biology, Economics)
        val topicName = subject.trim().ifEmpty { "General Studies" }
        val encoded = java.net.URLEncoder.encode(topicName, "UTF-8")
        return listOf(
            SubjectTopicNode(
                id = "dyn_1", order = 1, title = "1. Foundations & Core Concepts of $topicName",
                summary = "Fundamental definitions, historical/theoretical origins, and primary building blocks of $topicName.",
                flowSteps = listOf(
                    "Stage 1: Core Definitions, Scope & Key Terminology in $topicName",
                    "Stage 2: Foundational Principles, Laws & Classification Frameworks",
                    "Stage 3: Landmark Examples & Real-World Phenomena in $topicName",
                    "Stage 4: Concept Mapping & Summary Recall"
                ),
                resources = listOf(
                    LearningResourceLink("Khan Academy: $topicName Lessons", "https://www.khanacademy.org/search?page_search_query=$encoded"),
                    LearningResourceLink("Wikipedia: Comprehensive $topicName Guide", "https://en.wikipedia.org/w/index.php?search=$encoded")
                )
            ),
            SubjectTopicNode(
                id = "dyn_2", order = 2, title = "2. Analytical Methods & Core Mechanisms in $topicName",
                summary = "Cause-and-effect relationships, structured problem-solving methods, and key sub-disciplines.",
                flowSteps = listOf(
                    "Stage 1: Primary Sub-Topics & Structural Components of $topicName",
                    "Stage 2: Step-by-Step Analytical & Problem-Solving Techniques",
                    "Stage 3: Comparative Case Studies & Critical Perspectives",
                    "Stage 4: Guided Practice Questions & Edge-Case Analysis"
                ),
                resources = listOf(
                    LearningResourceLink("GeeksforGeeks: $topicName Articles & Notes", "https://www.geeksforgeeks.org/search/?q=$encoded"),
                    LearningResourceLink("Britannica: $topicName Encyclopedia Reference", "https://www.britannica.com/search?query=$encoded")
                )
            ),
            SubjectTopicNode(
                id = "dyn_3", order = 3, title = "3. Advanced Synthesis & Mastery in $topicName",
                summary = "Cross-topic connections, applied synthesis, and exam-grade mastery in $topicName.",
                flowSteps = listOf(
                    "Stage 1: Integrating Multiple Sub-Topics in Complex $topicName Scenarios",
                    "Stage 2: High-Yield Exam Patterns & Common Misconceptions",
                    "Stage 3: Applied Case Analysis & Independent Synthesis",
                    "Stage 4: 10-MCQ Diagnostic Verification in VicharaX"
                ),
                resources = listOf(
                    LearningResourceLink("MIT OpenCourseWare: $topicName Courses", "https://ocw.mit.edu/search/?q=$encoded"),
                    LearningResourceLink("Coursera / Open Study: $topicName", "https://www.coursera.org/search?query=$encoded")
                )
            )
        )
    }

    fun getDiagnosticQuestions(subject: String): List<DiagnosticQuestion> {
        val subjLower = subject.trim().lowercase()

        val rawQuestions: List<DiagnosticQuestion> = when {
            subjLower.contains("physic") -> listOf(
                DiagnosticQuestion(1, "Units & Dimensions", "SI Base Units",
                    "What is the SI unit of Force?",
                    listOf("Joule", "Newton", "Pascal", "Watt"), 1),
                DiagnosticQuestion(2, "Kinematics", "Motion in 1D",
                    "What does the slope of a velocity-time graph represent?",
                    listOf("Displacement", "Speed", "Acceleration", "Momentum"), 2),
                DiagnosticQuestion(3, "Newton's Laws", "Inertia & Mass",
                    "Which law of motion defines inertia and states that an object remains at rest or uniform motion unless acted on by a net external force?",
                    listOf("Newton's First Law", "Newton's Second Law", "Newton's Third Law", "Hooke's Law"), 0),
                DiagnosticQuestion(4, "Work & Energy", "Conservation Laws",
                    "What happens to the kinetic energy of a body if its velocity is doubled while mass stays constant?",
                    listOf("It doubles", "It halves", "It becomes four times", "It stays unchanged"), 2),
                DiagnosticQuestion(5, "Gravitation", "Inverse Square Law",
                    "What is the approximate acceleration due to gravity (g) near Earth's surface?",
                    listOf("4.9 m/s²", "9.8 m/s²", "19.6 m/s²", "1.6 m/s²"), 1),
                DiagnosticQuestion(6, "Electricity", "Ohm's Law",
                    "According to Ohm's Law, if resistance is constant and voltage is tripled, what happens to electric current?",
                    listOf("Current triples", "Current becomes one-third", "Current stays the same", "Current drops to zero"), 0),
                DiagnosticQuestion(7, "Waves & Sound", "Wave Properties",
                    "Which physical quantity is measured in Hertz (Hz)?",
                    listOf("Wavelength", "Amplitude", "Frequency", "Wave speed"), 2),
                DiagnosticQuestion(8, "Optics", "Refraction of Light",
                    "Which phenomenon causes a straw in a glass of water to appear bent at the water's surface?",
                    listOf("Diffraction", "Refraction", "Polarization", "Total internal reflection"), 1),
                DiagnosticQuestion(9, "Thermodynamics", "Heat Transfer",
                    "Which mode of heat transfer does NOT require any material medium and can travel through a vacuum?",
                    listOf("Conduction", "Convection", "Radiation", "Advection"), 2),
                DiagnosticQuestion(10, "Momentum", "Collisions",
                    "In the absence of external forces, what is always conserved in any collision between two bodies?",
                    listOf("Kinetic energy only", "Total linear momentum", "Velocity of each body", "Temperature"), 1)
            )

            subjLower.contains("math") -> listOf(
                DiagnosticQuestion(1, "Algebra", "Linear Equations",
                    "If 3x + 7 = 22, what is the value of x?",
                    listOf("3", "5", "7", "15"), 1),
                DiagnosticQuestion(2, "Quadratic Equations", "Roots & Discriminant",
                    "What is the discriminant (b² - 4ac) of the quadratic equation x² - 6x + 9 = 0?",
                    listOf("0", "36", "-36", "9"), 0),
                DiagnosticQuestion(3, "Trigonometry", "Fundamental Identities",
                    "What is the value of sin²(θ) + cos²(θ) for any real angle θ?",
                    listOf("0", "1", "2", "tan²(θ)"), 1),
                DiagnosticQuestion(4, "Calculus: Derivatives", "Power Rule",
                    "What is the derivative of f(x) = x³ with respect to x?",
                    listOf("x²", "3x²", "3x³", "x⁴ / 4"), 1),
                DiagnosticQuestion(5, "Calculus: Integration", "Antiderivatives",
                    "What is the definite integral ∫₀² 2x dx?",
                    listOf("2", "4", "6", "8"), 1),
                DiagnosticQuestion(6, "Probability", "Sample Space",
                    "When rolling a fair six-sided die once, what is the probability of getting an even number?",
                    listOf("1/6", "1/3", "1/2", "2/3"), 2),
                DiagnosticQuestion(7, "Matrices", "Determinants",
                    "What is the determinant of the 2×2 identity matrix [[1, 0], [0, 1]]?",
                    listOf("0", "1", "2", "-1"), 1),
                DiagnosticQuestion(8, "Geometry", "Circles",
                    "What is the area of a circle with radius r = 7 (using π ≈ 22/7)?",
                    listOf("44", "154", "308", "49"), 1),
                DiagnosticQuestion(9, "Logarithms", "Exponent Rules",
                    "What is the value of log₁₀(1000)?",
                    listOf("2", "3", "10", "100"), 1),
                DiagnosticQuestion(10, "Sequences & Series", "Arithmetic Progression",
                    "What is the 5th term of the arithmetic progression 2, 5, 8, 11, ...?",
                    listOf("13", "14", "15", "17"), 1)
            )

            subjLower.contains("c programming") || subjLower == "c" -> listOf(
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

            subjLower.contains("sql") || subjLower.contains("database") || subjLower.contains("dbms") -> listOf(
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

            subjLower.contains("data structure") || subjLower.contains("dsa") -> listOf(
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

            subjLower.contains("history") -> listOf(
                DiagnosticQuestion(1, "Ancient Civilizations", "Indus Valley",
                    "Which ancient civilization is renowned for planned grid cities like Mohenjo-daro and Harappa?",
                    listOf("Indus Valley Civilization", "Roman Empire", "Byzantine Empire", "Ottoman Empire"), 0),
                DiagnosticQuestion(2, "Mauryan Empire", "Ashoka",
                    "Which battle led Emperor Ashoka to renounce warfare and embrace Dhamma?",
                    listOf("Battle of Plassey", "Kalinga War", "Battle of Panipat", "Battle of Buxar"), 1),
                DiagnosticQuestion(3, "Mughal Empire", "Akbar",
                    "Who was the father of the Mughal Emperor Akbar?",
                    listOf("Babur", "Humayun", "Shah Jahan", "Aurangzeb"), 1),
                DiagnosticQuestion(4, "Medieval Architecture", "Monuments",
                    "Which Mughal ruler commissioned the Taj Mahal in Agra?",
                    listOf("Akbar", "Jahangir", "Shah Jahan", "Babur"), 2),
                DiagnosticQuestion(5, "Indian Independence", "1857 Uprising",
                    "In which year did the Indian Revolt / First War of Independence take place?",
                    listOf("1757", "1857", "1919", "1942"), 1),
                DiagnosticQuestion(6, "Freedom Movement", "Non-Cooperation",
                    "Which event in 1930 marked the beginning of the Civil Disobedience Movement led by Mahatma Gandhi?",
                    listOf("Quit India Movement", "Dandi Salt March", "Swadeshi Movement", "Champaran Satyagraha"), 1),
                DiagnosticQuestion(7, "Constitutional History", "Republic of India",
                    "On which date did the Constitution of India come into effect?",
                    listOf("15 August 1947", "26 November 1949", "26 January 1950", "2 October 1950"), 2),
                DiagnosticQuestion(8, "World History", "Industrial Revolution",
                    "In which country did the 18th-century Industrial Revolution first begin?",
                    listOf("France", "Germany", "Great Britain", "United States"), 2),
                DiagnosticQuestion(9, "World War II", "Global Events",
                    "In which year did World War II end?",
                    listOf("1918", "1939", "1945", "1950"), 2),
                DiagnosticQuestion(10, "Renaissance", "Cultural Rebirth",
                    "In which European country did the Renaissance originate during the 14th century?",
                    listOf("Spain", "Italy", "England", "Russia"), 1)
            )

            subjLower.contains("geography") -> listOf(
                DiagnosticQuestion(1, "Earth & Coordinates", "Latitude & Longitude",
                    "What is the 0° line of latitude that divides Earth into Northern and Southern Hemispheres?",
                    listOf("Prime Meridian", "Equator", "Tropic of Cancer", "International Date Line"), 1),
                DiagnosticQuestion(2, "Atmosphere", "Atmospheric Layers",
                    "In which layer of Earth's atmosphere does almost all weather occur?",
                    listOf("Stratosphere", "Mesosphere", "Troposphere", "Thermosphere"), 2),
                DiagnosticQuestion(3, "Plate Tectonics", "Earthquakes & Volcanoes",
                    "Which ocean basin is surrounded by the seismically active 'Ring of Fire'?",
                    listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"), 2),
                DiagnosticQuestion(4, "Rivers & Deltas", "Fluvial Landforms",
                    "Which is the largest river delta in the world, formed by the Ganga and Brahmaputra rivers?",
                    listOf("Nile Delta", "Sundarbans Delta", "Mississippi Delta", "Mekong Delta"), 1),
                DiagnosticQuestion(5, "Climate & Monsoons", "Seasonal Winds",
                    "Which seasonal wind system brings the majority of annual rainfall to the Indian subcontinent?",
                    listOf("Trade Winds", "Southwest Monsoon", "Polar Easterlies", "Mistral"), 1),
                DiagnosticQuestion(6, "Continents & Oceans", "Global Geography",
                    "Which is the largest continent by both land area and population?",
                    listOf("Africa", "North America", "Asia", "Europe"), 2),
                DiagnosticQuestion(7, "Mountain Ranges", "Orogeny",
                    "Which mountain range contains Mount Everest, the highest peak above sea level?",
                    listOf("Andes", "Rockies", "Himalayas", "Alps"), 2),
                DiagnosticQuestion(8, "Soils & Agriculture", "Pedology",
                    "Which fertile soil type is deposited by rivers in the Indo-Gangetic plains?",
                    listOf("Laterite Soil", "Alluvial Soil", "Desert Soil", "Peaty Soil"), 1),
                DiagnosticQuestion(9, "Ocean Currents", "Marine Geography",
                    "Which warm ocean current warms the climate of Western Europe?",
                    listOf("Labrador Current", "Gulf Stream", "Humboldt Current", "Benguela Current"), 1),
                DiagnosticQuestion(10, "Earth's Rotation", "Time Zones",
                    "How many degrees of longitude correspond to a 1-hour difference in local solar time?",
                    listOf("10°", "15°", "24°", "30°"), 1)
            )

            subjLower.contains("python") -> listOf(
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

            else -> {
                // Dynamic 10-MCQ generator tailored to ANY custom searched/selected topic
                val t = subject.trim().ifEmpty { "General Studies" }
                listOf(
                    DiagnosticQuestion(1, "Foundations of $t", "Core Terminology",
                        "What is the essential first step when learning the fundamentals of $t?",
                        listOf("Understand core definitions and foundational principles of $t", "Skip basic terminology completely", "Memorize unrelated topics first", "Avoid structured study"), 0),
                    DiagnosticQuestion(2, "Core Principles of $t", "Conceptual Clarity",
                        "Why are foundational principles critical in $t?",
                        listOf("They provide the mental framework needed for advanced $t problems", "They have no impact on problem solving", "They are only useful for historical trivia", "They replace practical application"), 0),
                    DiagnosticQuestion(3, "Classification in $t", "Structural Organization",
                        "How are subtopics in $t best organized for effective mastery?",
                        listOf("From prerequisite basics to intermediate analysis and applied synthesis", "In completely random order", "Starting only with unsolved research problems", "By ignoring foundational laws"), 0),
                    DiagnosticQuestion(4, "Analytical Reasoning in $t", "Cause & Effect",
                        "When analyzing a complex case or problem in $t, what approach yields the most reliable result?",
                        listOf("Break the problem into smaller components and verify each assumption", "Guess the final answer immediately", "Ignore boundary conditions", "Skip verification"), 0),
                    DiagnosticQuestion(5, "Practical Application of $t", "Applied Understanding",
                        "How can a learner verify true conceptual mastery in $t?",
                        listOf("Apply $t concepts to solve unseen problems and explain the reasoning", "Read notes passively without practice", "Avoid testing edge cases", "Rely solely on rote memorization"), 0),
                    DiagnosticQuestion(6, "Error Analysis in $t", "Debugging & Verification",
                        "What should you do when an initial solution in $t produces an unexpected outcome?",
                        listOf("Trace each step against core $t rules to isolate the flaw", "Ignore the discrepancy", "Assume all principles are invalid", "Abandon the problem"), 0),
                    DiagnosticQuestion(7, "Comparative Analysis in $t", "Methods & Approaches",
                        "Why is comparing multiple methods or perspectives valuable in $t?",
                        listOf("It reveals tradeoffs, efficiency differences, and optimal use cases", "It creates unnecessary confusion", "Only one perspective ever exists", "Comparison is discouraged"), 0),
                    DiagnosticQuestion(8, "Boundary & Edge Cases in $t", "Precision",
                        "What role do edge cases or exceptions play in mastering $t?",
                        listOf("They test the exact limits where standard rules behave differently", "They never occur in real scenarios", "They can always be ignored", "They contradict core theory"), 0),
                    DiagnosticQuestion(9, "Spaced Recall in $t", "Long-Term Retention",
                        "Which study habit produces the strongest long-term retention in $t?",
                        listOf("Active recall quizzes combined with spaced repetition", "Single cramming session without review", "Highlighting without self-testing", "Skipping mistake review"), 0),
                    DiagnosticQuestion(10, "Synthesis in $t", "End-to-End Mastery",
                        "What characterizes an advanced practitioner in $t?",
                        listOf("Connecting multiple $t subtopics to solve multi-step real-world problems", "Knowing isolated definitions without application", "Avoiding complex scenarios", "Never validating outcomes"), 0)
                )
            }
        }

        // Shuffle options across A, B, C, and D for every question while preserving exact correctIndex
        return rawQuestions.map { q ->
            makeShuffledQuestion(
                id = q.id,
                topic = q.topic,
                prerequisite = q.prerequisite,
                question = q.question,
                options = q.options,
                correctIndex = q.correctIndex
            )
        }
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
                prerequisite = node.flowSteps.firstOrNull() ?: "Core Foundations",
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
