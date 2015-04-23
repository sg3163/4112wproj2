# 4112wproj2
Esha Maharishi <em2852>
Sriharsha Gundappa <sg3163>

/* TO RUN */
To Compile AND Run with one command - Execute "make" command in the "src" directory

/* TO MODIFY INPUTS */
To change input, modify query.txt and config.txt

/* LANGUAGE */
The program is written in Java.

/* PROGRAM DESCRIPTION */

The majority of the algorithm is implemented across two classes-- Subset.java and BranchMispredAlgo.java, the latter of which contains the entry point (main() method). Subset.java is used to store and manipulate subset and their attributes, such as their associated cmetric and dmetric. Helper classes for wrapping information about the cmetric and dmetric were created, as was a class to wrap condition elements as pairs of (column, selectivity). In addition, these classes helped create a hierarchical way of printing by implementing their own toString methods.

The major parts of pre-processing, such as creating the power set and reading from input files, is done in BranchMispredAlgo. Traversing the 2^k array, whose elements are Subset objects as described above, is done by traversing the left and right nodes of Subset objects. The Subset class also provides ways to test set intersection and to retrieve the leftmost or rightmost child.

/* DIRECTORY STRUCTURE */
├── README.md
└── src
    ├── BranchMispredAlgo.java
    ├── CMetric.java
    ├── Condition.java
    ├── DMetric.java
    ├── Makefile
    ├── Subset.java
    ├── config.txt
    └── query.txt

A sample config, query, and output file have been included in this submission. When you run "make" in the src directory, the output from the compile commands is also printed; this output was deleted from the output.txt file before submitting.
