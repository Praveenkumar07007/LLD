# Singleton Design Pattern — Java

## 1. What is Singleton?

The Singleton Design Pattern is a **creational design pattern** whose goal is:

> Ensure that only one instance of a class exists and provide a global access point to that instance.

The basic structure is:

```text
                Singleton
                    |
          +---------+---------+
          |                   |
     private field       private constructor
          |
          v
      getInstance()
          |
          v
   same object every time
```

A Singleton normally has three important pieces:

1. A `private` constructor
2. A `static` field that stores the instance
3. A `static` `getInstance()` method that returns the instance

---

# 2. Why do we need a private constructor?

Suppose we write:

```java
Singleton s1 = new Singleton();
Singleton s2 = new Singleton();
```

Then Java creates two objects.

That breaks the Singleton requirement.

So we make the constructor private:

```java
private Singleton() {
}
```

Now outside code cannot do:

```java
new Singleton();
```

The class itself becomes responsible for creating its own object.

---

# 3. Lazy Initialization — NOT Thread-Safe

```java
class LazySingleton {

    private static LazySingleton instance;

    private LazySingleton() {
    }

    public static LazySingleton getInstance() {

        if (instance == null) {
            instance = new LazySingleton();
        }

        return instance;
    }
}
```

## What does lazy mean?

The object is created only when somebody asks for it.

Initially:

```text
instance = null
```

First call:

```java
LazySingleton.getInstance();
```

creates:

```text
new LazySingleton()
```

Second call returns the same object.

### Problem

This is **not thread-safe**.

Imagine two threads:

```text
Thread A                         Thread B

instance == null                 instance == null
       |                               |
       v                               v
new LazySingleton()              new LazySingleton()
```

Both threads can create an object.

Therefore:

```text
Singleton requirement
        X
Multiple instances possible
```

Do not use this implementation in a multithreaded application when uniqueness must be guaranteed.

---

# 4. Synchronized Singleton

```java
class SynchronizedSingleton {

    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {
    }

    public static synchronized SynchronizedSingleton getInstance() {

        if (instance == null) {
            instance = new SynchronizedSingleton();
        }

        return instance;
    }
}
```

## Why does this work?

The method is:

```java
public static synchronized
```

For a static synchronized method, Java uses the class-level monitor.

Only one thread can execute this method at a time.

So:

```text
Thread A -> gets lock -> creates object
Thread B -> waits
Thread A -> releases lock
Thread B -> gets lock -> sees object already exists
```

Therefore only one object is created.

## Problem

Suppose the object already exists.

Every call still does:

```text
getInstance()
     |
     v
acquire lock
     |
     v
return existing instance
     |
     v
release lock
```

The lock is no longer needed for the normal path.

Therefore this implementation is correct but can have unnecessary synchronization overhead.

---

# 5. Double-Checked Locking

A correct implementation is:

```java
class DoubleCheckedSingleton {

    private static volatile DoubleCheckedSingleton instance;

    private DoubleCheckedSingleton() {
    }

    public static DoubleCheckedSingleton getInstance() {

        if (instance == null) {

            synchronized (DoubleCheckedSingleton.class) {

                if (instance == null) {
                    instance = new DoubleCheckedSingleton();
                }
            }
        }

        return instance;
    }
}
```

## Why are there two checks?

The first check:

```java
if (instance == null)
```

avoids locking after the object has already been created.

The second check:

```java
if (instance == null)
```

is necessary because another thread could have created the object while the current thread was waiting for the lock.

Example:

```text
Thread A                         Thread B

instance == null                instance == null
       |                               |
       v                               v
   gets lock                      waits
       |
       v
creates instance
       |
       v
releases lock
                                       |
                                       v
                                  gets lock
                                       |
                                       v
                              second check == false
                                       |
                                       v
                              does NOT create object
```

## Why is `volatile` required?

This is a very important Java interview question.

```java
private static volatile DoubleCheckedSingleton instance;
```

`volatile` provides the required visibility and ordering guarantees for this publication pattern.

Without `volatile`, the JVM/compiler can reorder operations around object construction in a way that could allow another thread to observe an incompletely initialized object.

Therefore:

```text
Double-Checked Locking
        +
     volatile
        =
correct modern Java implementation
```

Do **not** write modern double-checked locking without `volatile`.

---

# 6. Eager Initialization

```java
class EagerSingleton {

    private static final EagerSingleton instance =
            new EagerSingleton();

    private EagerSingleton() {
    }

    public static EagerSingleton getInstance() {
        return instance;
    }
}
```

## Why is it called eager?

Because the object is created immediately when the class is initialized.

It does not wait for:

```java
getInstance()
```

The JVM's class initialization guarantees make this approach thread-safe.

## Advantage

Very simple.

## Disadvantage

The object is created even if nobody ever uses it.

Use it when the singleton is cheap to create and is expected to be needed.

---

# 7. Bill Pugh / Holder Class

```java
class HolderSingleton {

    private HolderSingleton() {
    }

    private static class Holder {

        private static final HolderSingleton INSTANCE =
                new HolderSingleton();
    }

    public static HolderSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

This is one of the cleanest class-based Singleton implementations.

## Why does it work?

The nested class:

```java
Holder
```

is initialized only when it is first accessed.

This means:

```java
Holder.INSTANCE
```

causes the Singleton to be created.

Before that, the Singleton object does not exist.

So we get:

```text
Lazy initialization
        +
Class initialization thread-safety
        +
No synchronized keyword
```

This is commonly called the **Bill Pugh Singleton** or **Initialization-on-demand Holder Idiom**.

---

# 8. Enum Singleton

```java
enum EnumSingleton {

    INSTANCE;

    public void doSomething() {
        System.out.println("Enum Singleton is doing something.");
    }
}
```

Usage:

```java
EnumSingleton singleton = EnumSingleton.INSTANCE;

singleton.doSomething();
```

There is no:

```java
private static EnumSingleton instance;
```

and no:

```java
getInstance()
```

The enum constant itself is the singleton instance.

## Why is enum attractive?

Java guarantees that an enum constant is instantiated only once per enum type.

It also gets strong serialization behavior from Java's enum mechanism and is resistant to normal reflection-based construction.

## Limitation

An enum cannot extend another class because every enum already extends:

```text
java.lang.Enum
```

So if your Singleton must inherit from another class, enum is not suitable.

---

# 9. Comparison

| Approach | Lazy? | Thread-safe? | Synchronization overhead | Complexity |
|---|---:|---:|---:|---:|
| Lazy | Yes | No | None | Very low |
| Synchronized method | Yes | Yes | Every call | Low |
| Double-checked locking | Yes | Yes | Only during initialization | Medium |
| Eager | No | Yes | None | Very low |
| Holder class | Yes | Yes | None explicitly | Low |
| Enum | JVM-managed | Yes | None explicitly | Very low |

---

# 10. What should I use in production?

There is no single answer for every situation.

### If enum fits the design

Prefer:

```java
enum EnumSingleton {
    INSTANCE;
}
```

It is extremely simple and robust.

### If you need a normal class

The Holder pattern is a strong simple option:

```java
class Singleton {

    private Singleton() {
    }

    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

### If you specifically need lazy initialization with explicit locking

Use double-checked locking with:

```java
private static volatile Singleton instance;
```

Do not omit `volatile`.

---

# 11. Important: Singleton does NOT automatically mean thread-safe

This is a common interview trap.

This:

```java
class Singleton {

    private static Singleton instance;

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }

        return instance;
    }
}
```

is a Singleton-looking class, but it is not safe under concurrent initialization.

You have to distinguish:

```text
Singleton
    =
Only one instance should exist

Thread-safe Singleton
    =
Only one instance should exist even
when multiple threads access it concurrently
```

Those are related but not identical requirements.

---

# 12. Why `volatile` matters in Double-Checked Locking

This is one of the most important interview topics.

Bad:

```java
private static Singleton instance;
```

Correct:

```java
private static volatile Singleton instance;
```

The important idea is:

```text
Thread A creates object
        |
        v
other threads must correctly see
the fully initialized object
```

`volatile` provides the visibility/order guarantees required by the modern Java Memory Model for this pattern.

---

# 13. `==` vs `equals()` when testing Singleton

When checking whether two references point to the exact same object:

```java
s1 == s2
```

is appropriate.

Example:

```java
Singleton s1 = Singleton.getInstance();
Singleton s2 = Singleton.getInstance();

System.out.println(s1 == s2);
```

Expected:

```text
true
```

Why?

Because `==` compares object references.

You are asking:

> "Are both variables pointing to the exact same object?"

---

# 14. Common Singleton interview questions

These are repeatedly appearing in Java interview preparation material:

### Q1. What is the Singleton pattern?

**Answer:**

> Singleton is a creational design pattern that restricts a class to one instance and provides a global access point to that instance.

### Q2. How do you make Singleton thread-safe?

**Answer:**

> We can use eager initialization, a synchronized `getInstance()`, the Holder class idiom, double-checked locking with `volatile`, or an enum. The choice depends on whether lazy initialization and inheritance flexibility are required.

### Q3. Why does double-checked locking require `volatile`?

**Answer:**

> `volatile` provides the visibility and ordering guarantees needed so that another thread does not observe a reference to a partially initialized object.

### Q4. Why do we check `instance == null` twice?

**Answer:**

> The first check avoids synchronization when the instance already exists. The second check prevents multiple threads that passed the first check from creating multiple instances after one of them acquires the lock.

### Q5. Why is `synchronized getInstance()` slower?

**Answer:**

> Because every invocation must acquire the synchronization mechanism, even after the singleton has already been initialized.

### Q6. What is the Bill Pugh Singleton?

**Answer:**

> It uses a private static nested Holder class containing the Singleton instance. The nested class is initialized only when first accessed, giving lazy initialization and thread safety through Java class initialization.

### Q7. Why is Enum Singleton considered robust?

**Answer:**

> The JVM controls enum instance creation, and enum serialization preserves the enum constant identity. It also avoids the normal reflection-based construction problem faced by class-based Singleton implementations.

### Q8. Can Singleton be broken by serialization?

For a normal serializable Singleton, yes. Deserialization can create a new object unless you protect the class appropriately, commonly using `readResolve()`.

### Q9. Can reflection break Singleton?

For normal class-based implementations, reflection can potentially access a private constructor and create another instance. Enum Singleton is much more resistant to this because enum construction is controlled by the JVM.

### Q10. Is Singleton always a good design?

No.

Singleton introduces global state and hidden dependencies, which can make testing and dependency management harder. In Spring applications, for example, you should also understand the difference between a Singleton design pattern and a Spring singleton-scoped bean.

---


