
package singletonmethod;

/*
 * ============================================================
 * SINGLETON DESIGN PATTERN - JAVA IMPLEMENTATIONS
 * ============================================================
 *
 *
 * Singleton goal:
 *      Ensure that only ONE instance of a class exists and
 *      provide a global access point to that instance.
 *
 * We demonstrate:
 *
 * 1. Lazy Initialization - NOT thread-safe
 * 2. Synchronized getInstance() - thread-safe, but every call
 *    acquires the lock
 * 3. Double-Checked Locking - lazy + thread-safe + efficient
 *    when implemented with volatile
 * 4. Eager Initialization - thread-safe and simple
 * 5. Bill Pugh / Holder Class - lazy + thread-safe + simple
 * 6. Enum Singleton - simplest robust option for many cases
 *
 * IMPORTANT:
 * The classes are package-private so they can all live in this
 * one Java source file. SingletonDemo is the public class.
 */
public class singletonDemo {

  public static void main(String[] args) {

    /*
     * ========================================================
     * 1. LAZY SINGLETON
     * ========================================================
     *
     * The object is NOT created when the class is loaded.
     * It is created only when getInstance() is called for
     * the first time.
     *
     * This implementation is NOT thread-safe.
     *
     * For example, if two threads execute getInstance()
     * at exactly the same time:
     *
     * Thread A -> instance == null -> creates object A
     * Thread B -> instance == null -> creates object B
     *
     * Result:
     *      More than one instance can be created.
     *
     * Use this implementation only to understand the problem.
     */
    LazySingleton lazy1 = LazySingleton.getInstance();
    LazySingleton lazy2 = LazySingleton.getInstance();

    System.out.println("Lazy Singleton same instance: " + (lazy1 == lazy2));


    /*
     * ========================================================
     * 2. SYNCHRONIZED SINGLETON
     * ========================================================
     *
     * The entire getInstance() method is synchronized.
     *
     * This guarantees that only one thread at a time can
     * execute the method.
     *
     * Therefore two threads cannot create two instances
     * simultaneously.
     *
     * Problem:
     * Even after the instance has already been created,
     * every future call still acquires the lock.
     *
     * That synchronization overhead may be unnecessary when
     * getInstance() is called very frequently.
     */
    SynchronizedSingleton sync1 = SynchronizedSingleton.getInstance();
    SynchronizedSingleton sync2 = SynchronizedSingleton.getInstance();

    System.out.println(
            "Synchronized Singleton same instance: "
                    + (sync1 == sync2)
    );


    /*
     * ========================================================
     * 3. DOUBLE-CHECKED LOCKING SINGLETON
     * ========================================================
     *
     * This is the corrected version of the common
     * double-checked locking implementation.
     *
     * IMPORTANT:
     * The instance field MUST be volatile.
     *
     * Why?
     * Because creating an object involves multiple operations:
     *
     *      1. Allocate memory
     *      2. Initialize the object
     *      3. Assign the reference to instance
     *
     * Without volatile, the JVM/compiler may reorder operations
     * in a way that allows another thread to observe the
     * reference before the object is completely initialized.
     *
     * First check:
     *      Avoid locking when the object already exists.
     *
     * Second check:
     *      Protect object creation when multiple threads
     *      reach the first check at the same time.
     *
     * This gives:
     *      Lazy initialization
     *      Thread safety
     *      No synchronization cost after initialization
     */
    DoubleCheckedSingleton dcl1 = DoubleCheckedSingleton.getInstance();
    DoubleCheckedSingleton dcl2 = DoubleCheckedSingleton.getInstance();

    System.out.println(
            "Double-Checked Singleton same instance: "
                    + (dcl1 == dcl2)
    );


    /*
     * ========================================================
     * 4. EAGER SINGLETON
     * ========================================================
     *
     * The instance is created immediately when the class is
     * initialized.
     *
     * The JVM guarantees safe class initialization, so this
     * approach is inherently thread-safe.
     *
     * Advantage:
     *      Very simple.
     *
     * Disadvantage:
     *      The object is created even if the application never
     *      needs it.
     *
     * Use this when the singleton is cheap to create and is
     * practically guaranteed to be needed.
     */
    EagerSingleton eager1 = EagerSingleton.getInstance();
    EagerSingleton eager2 = EagerSingleton.getInstance();

    System.out.println(
            "Eager Singleton same instance: "
                    + (eager1 == eager2)
    );


    /*
     * ========================================================
     * 5. BILL PUGH / HOLDER CLASS SINGLETON
     * ========================================================
     *
     * The singleton instance is stored inside a static nested
     * class.
     *
     * Java initializes the nested Holder class only when it is
     * first referenced.
     *
     * Therefore:
     *
     *      - Creation is lazy.
     *      - Class initialization provides thread safety.
     *      - No explicit synchronized block is required.
     *
     * This is a very clean class-based Singleton implementation.
     */
    HolderSingleton holder1 = HolderSingleton.getInstance();
    HolderSingleton holder2 = HolderSingleton.getInstance();

    System.out.println(
            "Holder Singleton same instance: "
                    + (holder1 == holder2)
    );


    /*
     * ========================================================
     * 6. ENUM SINGLETON
     * ========================================================
     *
     * Java enum constants are created by the JVM and each enum
     * constant exists exactly once per enum type.
     *
     * Enum Singleton is:
     *
     *      - Thread-safe
     *      - Very small and simple
     *      - Serialization-safe by Java's enum mechanism
     *      - Resistant to normal reflection-based construction
     *
     * Limitation:
     *      An enum cannot extend another class because every
     *      enum already extends java.lang.Enum.
     *
     * For many real-world cases, enum is the simplest robust
     * Singleton implementation.
     */
    EnumSingleton enum1 = EnumSingleton.INSTANCE;
    EnumSingleton enum2 = EnumSingleton.INSTANCE;

    System.out.println(
            "Enum Singleton same instance: "
                    + (enum1 == enum2)
    );

    enum1.doSomething();
  }
}


/*
 * ================================================================
 * 1. LAZY INITIALIZATION - NOT THREAD-SAFE
 * ================================================================
 *
 * The instance is created only when getInstance() is called.
 *
 * The constructor is private so external code cannot do:
 *
 *      new LazySingleton();
 *
 * However, the null check and object creation are not protected
 * from concurrent access.
 */
class LazySingleton {

  private static LazySingleton instance;

  private LazySingleton() {
    // Prevent external object creation.
  }

  public static LazySingleton getInstance() {

    if (instance == null) {
      instance = new LazySingleton();
    }

    return instance;
  }
}


/*
 * ================================================================
 * 2. SYNCHRONIZED SINGLETON
 * ================================================================
 *
 * The synchronized keyword ensures that only one thread can
 * execute getInstance() at a time.
 *
 * This prevents two threads from creating two objects.
 *
 * Trade-off:
 *      Every call to getInstance() obtains the class monitor lock,
 *      even after the singleton has already been initialized.
 */
class SynchronizedSingleton {

  private static SynchronizedSingleton instance;

  private SynchronizedSingleton() {
    // Prevent external object creation.
  }

  public static synchronized SynchronizedSingleton getInstance() {

    if (instance == null) {
      instance = new SynchronizedSingleton();
    }

    return instance;
  }
}


/*
 * ================================================================
 * 3. DOUBLE-CHECKED LOCKING SINGLETON
 * ================================================================
 *
 * This is the important corrected implementation.
 *
 * The volatile keyword is required.
 *
 * Why volatile?
 *
 * Without volatile, another thread can potentially observe the
 * instance reference before construction is fully visible to it.
 *
 * The algorithm:
 *
 *      if instance == null
 *          |
 *          +--> acquire lock
 *                  |
 *                  +--> if instance == null
 *                          |
 *                          +--> create object
 *
 * The first check avoids locking after initialization.
 *
 * The second check prevents two threads that both passed the first
 * check from creating two instances.
 */
class DoubleCheckedSingleton {

  private static volatile DoubleCheckedSingleton instance;

  private DoubleCheckedSingleton() {
    // Prevent external object creation.
  }

  public static DoubleCheckedSingleton getInstance() {

    /*
     * First check:
     *
     * If the object already exists, return it without
     * acquiring the lock.
     */
    if (instance == null) {

      /*
       * Only threads that see null enter this synchronized
       * block.
       */
      synchronized (DoubleCheckedSingleton.class) {

        /*
         * Second check:
         *
         * Another thread may have created the instance
         * while this thread was waiting for the lock.
         *
         * Therefore we MUST check again.
         */
        if (instance == null) {
          instance = new DoubleCheckedSingleton();
        }
      }
    }

    return instance;
  }
}


/*
 * ================================================================
 * 4. EAGER INITIALIZATION SINGLETON
 * ================================================================
 *
 * The singleton object is created when this class is initialized.
 *
 * The JVM guarantees that class initialization happens safely,
 * making this implementation thread-safe.
 *
 * There is no lazy initialization here.
 */
class EagerSingleton {

  /*
   * Instance is created immediately during class initialization.
   */
  private static final EagerSingleton instance = new EagerSingleton();

  private EagerSingleton() {
    // Prevent external object creation.
  }

  public static EagerSingleton getInstance() {
    return instance;
  }
}


/*
 * ================================================================
 * 5. BILL PUGH / HOLDER CLASS SINGLETON
 * ================================================================
 *
 * The Holder class is not initialized until getInstance() accesses
 * Holder.INSTANCE.
 *
 * This gives lazy initialization without explicit synchronization.
 *
 * Java's class initialization mechanism provides the required
 * thread-safety.
 */
class HolderSingleton {

  private HolderSingleton() {
    // Prevent external object creation.
  }

  /*
   * The nested class is initialized only when it is first used.
   */
  private static class Holder {
    private static final HolderSingleton INSTANCE =
            new HolderSingleton();
  }

  public static HolderSingleton getInstance() {
    return Holder.INSTANCE;
  }
}


/*
 * ================================================================
 * 6. ENUM SINGLETON
 * ================================================================
 *
 * The enum constant INSTANCE is the singleton instance.
 *
 * Usage:
 *
 *      EnumSingleton singleton = EnumSingleton.INSTANCE;
 *
 * No private constructor or getInstance() method is required.
 *
 * Java controls enum instance creation.
 */
enum EnumSingleton {

  INSTANCE;

  public void doSomething() {
    System.out.println("Enum Singleton is doing something.");
  }
}

