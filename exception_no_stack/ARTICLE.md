# All you wanted to know about Throwable

We use exceptions to handle something that is outside of the normal flow of the program. When an exception is thrown the
normal flow of the program is interrupted, and the execution stops dumping the exception to some output. These
exceptions can also be caught using the `try` and `catch` command pair built into the language. The exception itself is
an object in Java and can contain a lot of information. When we catch an exception in our code we have access to the
exception object and out code can act upon the exceptional situation also having access to the parameters that the
exception object is carrying. It is possible to implement our own exceptions extending the Java `java.lang.Throwable`
class or some of the classes that directly, or transitively extend `Throwable`. Our own implementation can hold many
parameters that describe the nature of the exceptional situation. We can use object fields for the purpose.

Although there is no limit for the data an exception can carry, it usually does not contain more than a message, and the
stack trace. There is room, as defined in the class `Throwable`, for other parameters, like the exception that was
causing the current one, or an array of suppressed exceptions. They are rarely used, presumably because developers are
not aware of these features and because most cases are simple and do not need these possibilities. We will have a look
at these possibilities as well in this article so that you will not belong to the group of those ignorant developers
who do not use these methods because they are not aware of it. 


 
 We, actually, will do that in this article, but first
let us focus on the stack trace.

The stack trace is an array of `java.lang.StackTraceElement` objects. This class is a data holding one with
hardly any functionality. It class has two public constructors.
One is from old times, before Java 9 and JPMS and a new one. The new has the arguments:

* `classLoaderName`
* `moduleName`
* `moduleVersion`
* `declaringClass`
* `methodName`
* `fileName`
* `lineNumber`

The first one is there for backward compatibility reasons, and the first three parameters are missing and
treated as `null`.  When an exception is thrown not only the location of the code where the error happened
is important, but also the location of the code from where the actual code was called. Also, where that code
was called and so on. This information is in the stack trace.

When we create a new exception the constructor calls a native method to fill in the stack trace. If you look
at the default constructor of the class `java.lang.Throwable` you can see that actually this is all it does:

```java
public Throwable() {
    fillInStackTrace();
}
```

The method `fillInStackTrace()` is not native but this is the method that actually invokes the native
`fillInStackTrace(int)` method that does the work. Here is how it is done in Java 14:

```java
public synchronized Throwable fillInStackTrace() {
    if (stackTrace != null ||
        backtrace != null /* Out of protocol state */ ) {
        fillInStackTrace(0);
        stackTrace = UNASSIGNED_STACK;
    }
    return this;
}
```

There is some "magic" in it, how it sets the field `stackTrace` but that is not really important as for now.
It is important, however to note that the method `fillInStackTrace()` is `public`. This means that it can be
overridden. If we have our own exception then we can an exception that dos  

