# Tiger

The compiler you will see, called Tiger, compiles a subset of the Java programming language, The major parts of the Tiger compiler are:

* Lexer and Parser
* Abstract Syntax Tree and Elaborator
* Code generators
* Garbage collector
* Optimizer
* Runtime and library

## 1. Lexer and Parser

### Lexer
The very first phase of a compiler is a lexer, which reads as input the program source files and outputs a series of tokens recognized. So the first step in designing and implementing a lexer is to design good data structures to represent the input and output, nevertheless to say, this task is implementation language dependent. In Java, the input can be represented as some kind of (buffered) input stream established from the program source text file, and the output token needs to be represented as another data structure.

### Parser
The task of a parser is to parse the input program, according to the given language syntax (production rules), and to answer whether or not the program beging parsed is legal (or meanwhile to generate some internal data structure as a result). In history, the parser may also generate target code directly, but recent compilers seldomly do this way, for the increasing complexivity of the languages and the increasing power of a modern computer.

## 2. Abstract Syntax Tree and Elaborator

### Abstract Syntax Tree
The sole purpose of a compiler is to process programs (which are being compiled). From the concept point of view, modern computers are based on the Von Neumann architecture, of which the most important feature is the stored program. That is, the program and data being processed must first be stored into the memeory, before the processing task can proceed. Compilers are of no exceptions: the program being compiled must first be stored into the main memory, before the compiler can start to compile it.

And the question is how. Maybe the most straightforward way to do this is to represent the program being compiled as a string of character (the program text as the programmer writes down), but this approach has the obvious drawback that later phases of the compiler would become more compilcated because it's nontrivial to recognize program phrase from the string (think how to find out whether or a variable x has been declared and where if if does. Obviously, this approach may involve complex string opereations).

A much more convenient way to store, in memory, the program being compiled is to use some specialized data structures from the implementation language. Nevertheless to say, this approach is highly implementation language-dependent. For instance, the representation technique from using Java would be different from that using C or other implementation languages.

### Elaborator
Before continuing to do other operations on the abstract syntax trees, one must check these trees to ensure they are well-formed. By the terminology well-formed, we mean that the input MiniJava program must obey all constraint rules specified by the MiniJava language specification (in turn by the Java specification). Typical rules include: a variable must be declared first before its use; the "+" operators must apply to two operands of integer type; the methods being invoked on an object must have been defined in its class or superclass; and so on. All such checking are preformed by an elaborator in the Tiger compiler. Note that in the literature, there are other names for the elaborator, say type checker or semantic analyzer, but we will call it an elaborator here in this lab.

## 3. Code Generators

### The C Code Generator
The first code generator will target the C programming language, that is, the generator will generate ANSI C code which can then be compiled into native code by a C compiler. Nevertheless to say, this is NOT the standard Java compilation pipeline, because we are generating native code from Java directly, instead of Java bytecode. However, it's profitable for you to build such a C code generator first: you will gain key insights into some key techniques in implementing modern OO language features, including single inheritance, virtual function tables and dynamic dispatching, etc.. From a historical prospective, early C++ compilers used to implement their code generators in a way similiar to the one in the Tiger compiler you're builing, one such example is Bjarne Stroustrup's CFront compiler, though this is not the case for modern C++ compilers. As a modern sample compiler for Java, the GNU's GCJ compiler for Java can generate native code directly.

### The Java Code Generator
In this part of the lab, your job is to design and implement a code generator generating Java bytecode targeting the Java virtual machine (JVM). Java bytecode is designed by Sun (now Oracle) and is the standard target code for Java. Though there are many JVMs available, the one we will use in the lab is the HotSpot JVM developed by Oracle (so you don't need to do further work of installation or configuration, as we have been using HotSpot to compile the Tiger compiler). However, there is nothing special with HotSpot, you can use other JVMs as long as they conform to the JVM specification.
[Jasmin homepage](http://jasmin.sourceforge.net/)

## 4. Garbage Collector

### GC Maps
In this lab, you will only need to consider garbage collection problems on uniprocessors, for it's much simpler than a collector on concurrent or realtime systems. During the execution of a Java application on a uniprocessor, the application allocates objects in the Java heap, and whenever the application need to allocate more objects but there is no enough space left in the Java heap, the application will be temporarily stopped (the so-called stop-the-world) and the garbage collector takes over to reclaim garbages in the Java heap. And after this round of collection, hopefully there is enough space available so that the application can resume.

### Copying Collection
In this part of the lab, you will write the Gimple garbage collector. You will first design and implement a Java heap, then you will design an object model, finally you will implement a copying collection algorithm based on Cheney's.

## 5. Optimizations

### Optimizations on the AST
Optimizations are program rewritings with the goal to make the optimized code faster, smaller, and so on. Optimizations can take different flavors: from imperative style to functional style and others. The optimizations you'll design and implement in this lab will make use of a functional style, in which a new program will be built (as the result of the optimization) without modifying the old program (the program being optimized). It'll be easier to write, debug, and maintain your code to use this style. Don't worry about what a functional style is, if you have not heard of it before. After this lab, you will get a deep understanding of the functional programming style and how to program in it (using OO).

Optimizations are higly dependent on the specific intermediate representation (IR) used: an optimzation may be easy to perform on some kinds of IRs but difficult, if not impossible, on other kinds of IRs. Strictly speaking, as a high-level intermediate representation, abstract syntax trees are not a good IR for the purpose of optimizations, because the control/data flow information are obscure on an AST. As a result, only a few optimization are eligible for ASTs. However, it's not a bad idea for you to write optimizations on the AST as a starting point: first, it will give you some general ideas about how the functional-style optimization strategy work throughout this lab; second, the optimizations you will write on the ASTs, on some cases, will simplify the AST dramastically and thus make later phases of the compiler simpler.

### Control-flow Graph (CFG)
A control-flow graph (CFG) is a graph-based program representation with basic blocks as nodes and control transfers between blocks as edges. A control-flow graph is a good candidate intermediate representation for it makes optimizations easier to perform. In this part of the lab, you will construct the control-flow graph representation of a given program, and write various optimizations on CFGs.

### CFG-based Optimizations
CFG-based program representations make program optimizations much easier, due to the fact that it's much easier to calculate the control flow and data flow information required to perform optimizations. Generally speaking, an optimization can be divided into two phases: program analysis and rewriting. During the program analysis phase (usually data-flow analysis), the compiler analyzes the target program to get useful static information as an approximation of the program's dynamic behaviour. And such information will be used in the second phase: program rewriting, the compiler rewrites the program according to the static information obtained in the first phase. As a result fo these two steps, the program is altered in some way---it is optimized. For instance, in the dead-code elimination optimization, the program analysis phase will determine whether or not the variable x defined by x = e will be used anywhere in this program; if x will not be used, the program rewriting phase will remove this assignment from this program (and the program shrinks).

In this part , there are several classical program analysis: liveness analysis, reaching definitions, available expressions, and so on and several classical optimizations (based on these analysis): constant propagation, copy propagation, dead code elimination, etc..



