# All you wanted to know about Throwable

## Introduction

In this article we will talk about exceptions and what we can and should do with Java exceptions. The simplest case is
to throw one and then to catch it, but there are more complex situations, like setting a cause or suppressed exceptions.
We will look at these possibilities, and a bit more. To discover the possibilities we will develop a simple application
and step-by-step we will create four versions developing the application further and further using more and more
exception handling possibilities. The source code is available in the repository:

https://github.com/verhas/BLOG/tree/master/exception_no_stack

The different versions are in different Java packages. Some classes that did not change in the different versions are
one package higher, and they are not versioned.

* The first version `v1` simply throws en exception, and it is not handled by the application. The test code expects the
  test setup to throw the exception. This version is the baseline to demonstrate why we need more complex solutions. We
  will experience that there is not enough information in the exception to see where the actual issue has happened.
  
* The second version `v2` catches the exception at higher levels and throws new exception with more information about the
  exceptional case and the new exception has the original one embedded as cause. This approach gives enough information
  to track the location of the issue, but it can even be enhanced so that it is easier to read and recognize the actual
  problem.
  
* The third version `v3` will demonstrate how we can modify the creation of the new exceptions so that the stack trace
  of the higher level exceptions will not point to the location where the original exception was caught, but rather
  where the original exception was thrown.

* Finally, the fourth version `v4` will demonstrate how we can suppress expressions when it is possible to go on with
  the processing in case of en exceptional case even if the operation cannot be finished successfully. This "going
  further" makes it possible to have an exception at the end that collects the information about all discovered
  exceptional cases and not only the first occurrence.

If you look at the code, you will also find there the original text of this article, and the setup that helps to
maintain the code snippets copying them into the article from the source keeping all of them up-to-date. The tool that
does it for us is Java::Geci.

## Sample Application 

We use exceptions to handle something that is outside of the normal flow of the program. When an exception is thrown the
normal flow of the program is interrupted, and the execution stops dumping the exception to some output. These
exceptions can also be caught using the `try` and `catch` command pair built into the language. 

```java
    try {
        ... some code ...
        ... even calling methods
                      several level deep     ...
        ...    where exception may be thrown ...
      }catch(SomeException e){
        ... code having access to the exception object 'e'
            and doing someting with it (handling) ....
      }
```

The exception itself is an object in Java and can contain a lot of information. When we catch an exception in our code,
we have access to the exception object, and the code can act upon the exceptional situation also having access to the
parameters that the exception object is carrying. It is possible to implement our own exceptions extending the Java
`java.lang.Throwable` class or some of the classes that directly, or transitively extend `Throwable`. (Usually we extend
the class `Exception`.) Our own implementation can hold many parameters that describe the nature of the exceptional
situation. We use object fields for the purpose.

Although there is no limit for the data an exception can carry, it usually does not contain more than a message, and the
stack trace. There is room, as defined in the class `Throwable`, for other parameters, like the exception that was
causing the current one (`getCause()`), or an array of suppressed exceptions (`getSuppressed()`). They are rarely used,
presumably because developers are not aware of these features and because most cases are simple and do not need these
possibilities. We will have a look at these possibilities in this article so that you will not belong to the group of
those ignorant developers who do not use these methods only because they are not aware of it.

We have a sample application. It is a bit more than just throwing and catching an exception. Throwing and catching
and exception and doing something in the `catch` branch that lets the code to continue is simple and is explained in 
the tutorial you have read when learning to program in Java the first time.

Our sample application will be a bit more complex. We will list the files in a directory, read the lines, and count the
number of `wtf` strings. This way we automate the code review process quality measurement (joking). It is said that the
code quality is reverse proportional with the number of the WTFs during the code review.

The solution contains

* a `FileLister` that can list the files,
* a `FileReader` that can read a file,
* a `LineWtfCounter` that will count the `wtf`s in a single line,
* a `FileWtfCounter` that will use the previous class to count all the `wtf`s in the whole file listing the lines, and
  finally,
* a `ProjectWtfCounter` that counts the `wtf`s in the whole project using the file level counter, listing all the files.


### Version 1, throw and catch

The application functionality is fairly simple and because we focus on the exception handling the implementation is
also trivial. For example, the file listing class is as simple as the following:

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
not need anything more complex to demonstrate the exception handling.

The real counter, which counts the number of `wtf` occurrences in a line is

<!-- snip LineWtfCounter skip="do"-->
```java
package javax0.blog.demo.throwable.v1;

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

To save space and focus on our topic the snippet does not display the actual logic (was automatically removed by
Java::Geci). The reader can create a code that actually counts the number of `wtf` substrings in a string, or else
simply "wtf". Even if the reader cannot write such a code it is available from the repository mentioned at the start
of the article.

-----------
The logic in out application says that this is an exceptional situation if one of the lines in the file has zero length.
In that case we throw an exception.
-----------

Usually such a situation does not verify to be an exception, and I acknowledge that this is a bit contrived example, but
we needed something simple. If the length of the line is zero then we throw a `LineEmpty` exception. (We do not list the
code of `LineEmpty` exception. It is in the code repo, and it is simple, nothing special. It extends `RuntimeException`,
no need to declare where we throw it.)

The counter on the file level using the line level counter is the following: 

<!-- snip FileWtfCounter_v1  skip="do"-->
```java
package javax0.blog.demo.throwable.v1;

public class FileWtfCounter {
    // fileReader injection is not listed
    public int count() {
        final var lines = fileReader.list();
        int sum = 0;
        for (final var line : lines) {
            sum += new LineWtfCounter(line).count();
        }
        return sum;
    }

}

```

(Again, some trivial lines are skipped from the printout.)

This is the first version of the application. It does not have any special exception handling. It just sums up the
values that the line counters return and in case there is an exception on the lower level, in the line `wtf` counter
then this will automatically propagate up. We do not handle that exception in any way on this level.

The project level counter is very similar. It uses the file counter and sums up the results. 

<!-- snip ProjectWftCounter_v1   skip="do"-->
```java
package javax0.blog.demo.throwable.v1;

import javax0.blog.demo.throwable.FileLister;

public class ProjectWftCounter {
    // fileLister injection is not listed
    public int count() {
        final var fileNames = fileLister.list();
        int sum = 0;
        for (final var fileName : fileNames) {
            sum += new FileWtfCounter(new FileReader(fileName)).count();
        }
        return sum;
    }
}

```

We test it using the simple test code:

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
A unit test usually should not have a stack trace print. In this case we have it to demonstrate what is thrown.
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

There is a little problem with this exception. When we use this code it does not tell us anything about the actual file
and line that is the problematic. We have to examine all the files and all the lines if there is an empty one. It is
not too difficult to write an application for that, but we do not want to work instead of the programmer who created
the application. When there is an exception we expect the exception to give us enough information to successfully tackle
the situation. The application has to tell me which file and which line is the faulty.

### Version 2, setting cause

To provide the information in the exception we have to gather it and insert into the exception. This is what we do in
the second version of the application.

The exception in the first version does not contain the name of the file, or the line number because the code does not
put it there. The code has a good reason to do that. The code at the location of the exception throwing does not have
the information and thus it cannot insert into the exception what it does not have.

A lucrative approach could be to pass this information along with the other parameters, so that when an exception
happens the code can insert this information into the exception. I do not recommend that approach. If you look at the
source codes I published on GitHub you may find examples of this practice. I am not proud of them, and I am sorry.
Generally, I recommend that the exception handling should not interfere with the main data flow of the application. It
has to be separated as it is a separate concern.

The solution is to handle the exception on several levels, on each level adding the information, which is available at
the actual level. To do that we modify the classes `FileWtfCounter` and `ProjectWftCounter`.

The code of `ProjectWftCounter` becomes the following:

<!-- snip FileWtfCounter_v2 skip="do"-->
```java
package javax0.blog.demo.throwable.v2;

public class FileWtfCounter {
    // some lines deleted ...
    public int count() {
        final var lines = fileReader.list();
        int sum = 0;
        int lineNr = 1;
        for (final var line : lines) {
            try {
                sum += new LineWtfCounter(line).count();
            }catch(LineEmpty le){
                throw new NumberedLineEmpty(lineNr,le);
            }
            lineNr ++;
        }
        return sum;
    }

}
```

The code catches the exception that signals the empty line and throws a new one, which already has a parameter: the
serial number of the line.

The code for this exception is not so trivial as in the case of `LineEmpty`, thus it is listed here:

<!-- snip NumberedLineEmpty_v2 -->
```java
package javax0.blog.demo.throwable.v2;

public class NumberedLineEmpty extends LineEmpty {
    final protected int lineNr;

    public NumberedLineEmpty(int lineNr, LineEmpty cause) {
        super(cause);
        this.lineNr = lineNr;
    }

    @Override
    public String getMessage() {
        return "line " + lineNr + ". has zero length";
    }
}
```

We store the line number in an `int` field, which is `final`. We do it because

* use `final` variables if possible
* use primitives over objects if possible
* store the information in the natural so long as long it is possible so that the use of it is not limited.

The first two criteria are general. The last one is special in this case, although it is not specific to exception
handling. When we are handling exceptions, however, it is very lucrative to just generate a message that contains the
line number instead of complicating the structure of the exception class. After all, the reasoning that we will never
use the exception for anything else than printing it to screen is valid. Or not? It depends. First of all, never say
never. Second thought: if we encode the line number into the message then it is certain that we will not ever use it
for anything else than printing it to the user. That is because we cannot use it for anything else. We limit ourselves.
The today programmer limits the future programmer to do something meaningful with the data.

You may argue tha this is YAGNI. We should care about storing the line number as an integer when we want to use it and
caring about it at the very moment is too early and is just waste of time. You are right! The same time someone who is
creating the extra field and the `getMessage()` method that calculates the text version of the exception information is
also right. Sometimes there is a very thin line between YAGNI and careful and good style programming. YAGNI is to avoid
complex code that later you will not need (except that when you create it, you think that you will need). In this
example I have the opinion that the above exception with that one extra `int` field is not "complex".

We have the similar code on the "project" level, where we handle all the files. The code of `ProjectWftCounter` will be

<!-- snip ProjectWftCounter_v2 skip="do"--> 
```java
package javax0.blog.demo.throwable.v2;

import javax0.blog.demo.throwable.FileLister;

public class ProjectWftCounter {
    // some lines deleted ...
    public int count() {
        final var fileNames = fileLister.list();
        int sum = 0;
        for (final var fileName : fileNames) {
            try {
                sum += new FileWtfCounter(new FileReader(fileName)).count();
            } catch (NumberedLineEmpty nle) {
                throw new FileNumberedLineEmpty(fileName, nle);
            }
        }
        return sum;
    }
}
``` 

Here we know the name of the file and thus we can extend the information adding it to the exception.

The exception `FileNumberedLineEmpty` is also similar to the code of `NumberedLineEmpty`. Here is the code of
`FileNumberedLineEmpty`:

<!-- snip FileNumberedLineEmpty_v2 -->
```java
package javax0.blog.demo.throwable.v2;

public class FileNumberedLineEmpty extends NumberedLineEmpty {
    final protected String fileName;

    public FileNumberedLineEmpty(String fileName, NumberedLineEmpty cause) {
        super(cause.lineNr, cause);
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        return fileName + ":" + lineNr + " is empty";
    }
}
```

At this moment I would draw your focus to the fact that the exceptions that we created are also in inheritance
hierarchy. They extend the other as the information we gather and store is extended, thus:

```
FileNumberedLineEmpty - extends -> NumberedLineEmpty - extends -> LineEmpty
```

If the code using these methods expects and tries to handle a `LineEmpty` exception then it can do even if we throw a
more detailed and specialized exception. If a code wants to use the extra information then it, eventually, has to know
that the actual instance is not `LineEmpty` rather something more specialized as `NumberedLineEmpty` or
`FileNumberedLineEmpty`. However, if it only wants to print it out, get the message then it is absolutely fine to handle
the actual instance as an instance of `LineEmpty`. Even doing so the message will contain the extra information in 
human readable form thanks to OO programming polymorphism.

The proof of the pudding is the eating. We can run our code with the simple test. The test code is the same as it was
in the previous version  with the only exception that the expected exception type is `FileNumberedLineEmpty` instead of
`LineEmpty`. The printout, however, is interesting:

```
javax0.blog.demo.throwable.v2.FileNumberedLineEmpty: c.txt:4 is empty
	at javax0.blog.demo.throwable.v2.ProjectWftCounter.count(ProjectWftCounter.java:22)
	at javax0.blog.demo.throwable.v2.TestWtfCounter.lambda$testThrowing$0(TestWtfCounter.java:17)
	at org.assertj.core.api.ThrowableAssert.catchThrowable(ThrowableAssert.java:62)
...
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
Caused by: javax0.blog.demo.throwable.v2.NumberedLineEmpty: line 4. has zero length
	at javax0.blog.demo.throwable.v2.FileWtfCounter.count(FileWtfCounter.java:21)
	at javax0.blog.demo.throwable.v2.ProjectWftCounter.count(ProjectWftCounter.java:20)
	... 68 more
Caused by: javax0.blog.demo.throwable.v2.LineEmpty: There is a zero length line
	at javax0.blog.demo.throwable.v2.LineWtfCounter.count(LineWtfCounter.java:15)
	at javax0.blog.demo.throwable.v2.FileWtfCounter.count(FileWtfCounter.java:19)
	... 69 more
```

We can be happy with this result as we immediately see that the file, which is causing the problem is `c.txt` and the
fourth line is the one, which is the culprit. On the other hand, we cannot be happy when we want to have a look at the
code that was throwing the exception. Some time in the future we may not remember why a line must not have zero length.
In that case we want to look at the code. There we will only see that an exception is caught and rethrown. Luckily there
is the cause, but it is actually three steps till we get to the code that is the real problem at
`LineWtfCounter.java:15`.

Will anyone ever be interested in the code that is catching an rethrowing an exception? May be yes. May be no. In our
case we decide that there will not be anyone interested in that code and instead of handling a long chain of exception
listing the causation of the guilty we change the stack trace of the exception that we throw to that of the causing
exception.

### Version 3, setting the stack trace

In this version we only change the code of the two exceptions: `NumberedLineEmpty` and `FileNumberedLineEmpty`. Now they
not only extend one the other and the other one `LineEmpty` but they also set their own stack trace to the value that
the causing exception was holding.

Here is the new version of `NumberedLineEmpty`:

<!-- snip NumberedLineEmpty_v3 skip="do"-->
```java
package javax0.blog.demo.throwable.v3;

public class NumberedLineEmpty extends LineEmpty {
    final protected int lineNr;

    public NumberedLineEmpty(int lineNr, LineEmpty cause) {
        super(cause);
        this.setStackTrace(cause.getStackTrace());
        this.lineNr = lineNr;
    }

    // getMessage() same as in v2

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
```

Here is the new version of `FileNumberedLineEmpty`:

<!-- snip FileNumberedLineEmpty_v3 skip="do"-->
```java
package javax0.blog.demo.throwable.v3;

public class FileNumberedLineEmpty extends NumberedLineEmpty {
    final protected String fileName;

    public FileNumberedLineEmpty(String fileName, NumberedLineEmpty cause) {
        super(cause.lineNr, cause);
        this.setStackTrace(cause.getStackTrace());
        this.fileName = fileName;
    }

    // getMessage(), same as in v2
```


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

