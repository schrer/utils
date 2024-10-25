# Java Utils

Implementations for a few utility classes, like data structures and infrastructure for dependency injection with no dependencies outside of standard JVM components.  
None of it is really well tested or even completely implemented. It is just a little pastime, to refresh some knowledge and try some things.  
I did not look up best practices for most of the things I implemented. But at least I wrote some test cases!

## Contents

- A [StringUtils](./src/main/java/at/schrer/utils/StringUtils.java) implementation, containing some more normal functions (reverse, removeChar) and some more nonsensical ones (sort, sum)
- A linked list implementation at [SomeList](./src/main/java/at/schrer/structures/SomeList.java), implementing the java.util.List interface.
- The [ClassScanner](./src/main/java/at/schrer/inject/ClassScanner.java), which is able to return a list of classes under a provided package name. The classes can also be filtered by annotations.
- A class called [ContextBuilder](./src/main/java/at/schrer/inject/ContextBuilder.java), which can instantiate classes from a provided package that are marked with the annotation [@Component](./src/main/java/at/schrer/inject/annotations/Component.java). The ContextBuilder supports constructor-injection of dependencies. It does deliberately not support cyclic dependencies.
- An implementation of an [acyclic graph](./src/main/java/at/schrer/structures/SomeAcyclicGraph.java). It is a simple free implementation used in the ContextBuilder to build the dependency graph between all components.
- A map implementation called [SomeMap](./src/main/java/at/schrer/structures/SomeMap.java). It uses an array internally and is not very smart. But it seems to work so far.