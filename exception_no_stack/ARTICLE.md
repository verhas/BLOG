# All you wanted to know about Throwable

## Introduction

In this article we will talk about exceptions and what we can and should do with Java exceptions. The simplest case is
to throw one and then to catch it, but there are more complex situations, like setting a cause or suppressed exceptions.
We will look at these possibilities, and a bit more. To discover the possibilities we will develop a simple application
and step-by-step we will create four versions developing the application further and further using more and more
exception handling possibilities. The source code is available in the repository:

https://github.com/verhas/BLOG/tree/master/exception_no_stack

The different versions are in different Java packages. Some classes that did not change in the different versions are
one package higher and they are not versioned.

If you look at the code, you will also find there the original text of this article, and the setup that helps to
maintain the code snippets copying them into the article from the source keeping all of them up-to-date. The tool that
does it for us is Java::Geci.

## Sample Application 

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

We will have a sample application that is a more than just throwing and catching an exception. Throwing and catching
and exception and doing something in the `catch` branch that lets the code to continue is simple and is explained in 
the tutorial you have read when learning to program in Java the first time.

Our sample application will be a bit more complex. We will list the files in a directory, read the lines, and count the
number of `wtf` strings. This way we automate the code review process quality measurement. It is said that the code
quality is reverse proportional with the number of the WTFs during the code review.

The solution is simple. We will need

* a `FileLister` that can list the files,
* a `FileReader` that can read a file,
* a `LineWtfCounter` that will count the `wtf`s in a single line,
* a `FileWtfCounter` that will use the previous class to count all the `wtf`s in the whole file, and finally,
* a `ProjectWtfCounter` that counts the `wtf`s in the whole project using the file level counter.
 
The application functionality is fairly simple and because we focus on the exception handling the implementation is also.
For example, the file listing class is as simple as the following:

<!-- snip FileLister trim="to=0"-->
```java
package javax0.blog.demo.throwable;

import java.util.List;

public class FileLister {

    public FileLister() {
    }

    public List<String> list() {
        return List.of("a.txt", "b.txt", "c.txt");
    }
}
```

We have three files in the file system, `a.txt`, `b.txt`, and `c.txt`. This is a mock, of course, but in this case we do
not need anything more complex.

The real counter, which counts the number of `wtf` occurrences in a line is

<!-- snip LineWtfCounter skip="do"-->
```java
package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.Counter;

public class LineWtfCounter {
    private final String line;

    public LineWtfCounter(String line) {
        this.line = line;
    }

    public static final String WTF = "wtf";
    public static final int WTF_LEN = WTF.length();

    public int count() {
        if (line.length() == 0) {
            throw new LineEmpty();
        }
        // the actual lines are removed from the documentation snippet
    }

}

```

To save space and focus on our topic the snippet does not display the actual logic. The reader can create a code that
actually counts the number of `wtf` substrings in a string, or else simply "wtf". Even if the reader cannot write such a code
it is available from

https://github.com/verhas/BLOG/tree/master/exception_no_stack

The logic in out application says that this is an exceptional situation if one of the lines in the file has zero length.
In that case we throw an exception. Usually such a situation does not verify to be an exception, and I acknowledge that
this is a bit contrived example, but I wanted to keep is simple.

The counter on the file level using the line level counter is the following: 

<!-- snip FileWtfCounter_v1 -->
```java                 
package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.Counter;

import java.io.FileNotFoundException;

public class FileWtfCounter {
    private final FileReader fileReader;

    public FileWtfCounter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public int count() throws FileNotFoundException {
        final var lines = fileReader.list();
        int sum = 0;
        for (final var line : lines) {
            sum += new LineWtfCounter(line).count();
        }
        return sum;
    }

}

```

This is the first version of the application. It does not have any special exception handling. It just sums up the
values that the line counters return and in case there is an exception on the lower level, in the line `wtf` counter
then this will automatically propagate up. We do not handle that exception in any way on this level.

The project level counter is very similar. It uses the file counter and sums up the results. 

<!-- snip ProjectWftCounter_v1 -->
```java                 
package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.Counter;
import javax0.blog.demo.throwable.FileLister;

import java.io.FileNotFoundException;

public class ProjectWftCounter {

    private final FileLister fileLister;

    public ProjectWftCounter(FileLister fileLister) {
        this.fileLister = fileLister;
    }


    public int count() throws FileNotFoundException {
        final var fileNames = fileLister.list();
        int sum = 0;
        for (final var fileName : fileNames) {
            sum += new FileWtfCounter(new FileReader(fileName)).count();
        }
        return sum;
    }
}

```

When we test that using the simple test code:

<!-- snip TestWtfCounter_v1 -->
```java                 
package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.FileLister;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestWtfCounter {

    @Test
    @DisplayName("Throws up for a zero length line")
    void testThrowing() {
        Throwable thrown = catchThrowable(() ->
                new ProjectWftCounter(new FileLister())
                        .count());
        assertThat(thrown).isInstanceOf(LineEmpty.class);
        thrown.printStackTrace();
    }

}

```

The stack trace in the error will show us the error as the following:

```
javax0.blog.demo.throwable.v1.LineEmpty: There is a zero length line
	at javax0.blog.demo.throwable.v1.LineWtfCounter.count(LineWtfCounter.java:18)
	at javax0.blog.demo.throwable.v1.FileWtfCounter.count(FileWtfCounter.java:19)
	at javax0.blog.demo.throwable.v1.ProjectWftCounter.count(ProjectWftCounter.java:22)
	at javax0.blog.demo.throwable.v1.TestWtfCounter.lambda$testThrowing$0(TestWtfCounter.java:18)
	at org.assertj.core.api.ThrowableAssert.catchThrowable(ThrowableAssert.java:62)
    ...
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
```

There is a little slight problem with this one.


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

