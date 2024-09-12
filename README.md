# Java Utils

Implementations for a few utility classes, like data structures and infrastructure for dependency injection with no dependencies outside of standard JVM components.  
None of it is really well tested or even completely implemented. It is just a little pastime, torefresh some knowledge and try some things.  
I did not look up best practices for most of the things I implemented.

## Contents

- A linked list implementation at [SomeList](./src/main/java/at/schrer/structures/SomeList.java), implementing the java.util.List interface.
- The [ClassScanner](./src/main/java/at/schrer/inject/ClassScanner.java), which is able to return a list of classes under a provided package name. The classes can also be filtered by annotations.
- A class called [ContextBuilder](./src/main/java/at/schrer/inject/ContextBuilder.java), which can instantiate classes from a provided package that are marked with the annotation [@Component](./src/main/java/at/schrer/inject/annotations/Component.java). Currently, it can only instantiate classes that have a no-args constructor.
