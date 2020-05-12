# All you wanted to know about Throwable

This article is a tutorial about exceptions. But not the usual one. There are many of those that tell you what exceptions are for, how you can throw one, catch one, the difference between checked and runtime exceptions, and so on. There is no need for another. It would also be boring for you. If not, then go and read one of those and come back when you have learned what they teach. This article starts where those tutorials end. We dive a bit deeper into Java exceptions, what you can do with them, what you should do with them, and what features they have that you may not have heard about. If `setStackTrace()`, `getCause()` and `getSuppressed()` are the methods you eat for breakfast then you can skip this article. But if not, and you want to know a bit about these, then go on. This article is long. It took a long time to write, and it will take a long time to read. It is needed.

## Introduction

In this article, we will talk about exceptions and what we can and should do with Java exceptions. The simplest case is to throw one and then catch it, but there are more complex situations, like setting a cause or suppressed exceptions. We will look at these possibilities, and a bit more. To discover the possibilities we will develop a simple application and step-by-step we will create four versions developing the application further and further using more and more exception handling possibilities. The source code is available in the repository:

https://github.com/verhas/BLOG/tree/master/exception_no_stack

The different versions are in different Java packages. Some classes that did not change in the different versions are one package higher, and they are not versioned.

* The first version `v1` simply throws en exception, and it is not handled by the application. The test code expects the test setup to throw the exception. This version is the baseline to demonstrate why we need more complex solutions. We will experience that there is not enough information in the exception to see where the actual issue has happened.
  
* The second version `v2` catches the exception at higher levels and throws a new exception with more information about the exceptional case, and the new exception has the original one embedded as cause. This approach gives enough information to track the location of the issue, but it can even be enhanced so that it is easier to read and recognize the actual problem.
  
* The third version `v3` will demonstrate how we can modify the creation of the new exceptions so that the stack trace of the higher level exceptions will not point to the location where the original exception was caught, but rather where the original exception was thrown.

* Finally, the fourth version `v4` will demonstrate how we can suppress expressions when it is possible to go on with the processing in case of en exceptional case even if the operation cannot be finished successfully. This "going further" makes it possible to have an exception at the end that collects the information about all discovered exceptional cases and not only the first occurrence.

If you look at the code, you will also find there the original text of this article, and the setup that helps to maintain the code snippets copying them into the article from the source keeping all of them up-to-date. The tool that does it for us is Java::Geci.

## Sample Application 

We use exceptions to handle something that is outside of the normal flow of the program. When an exception is thrown the normal flow of the program is interrupted, and the execution stops dumping the exception to some output. These exceptions can also be caught using the `try` and `catch` command pair built into the language. 

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

The exception itself is an object in Java and can contain a lot of information. When we catch an exception in our code, we have access to the exception object, and the code can act upon the exceptional situation also having access to the parameters that the exception object is carrying. It is possible to implement our own exceptions extending the Java
`java.lang.Throwable` class or some of the classes that directly, or transitively extend `Throwable`. (Usually, we extend the class `Exception`.) Our own implementation can hold many parameters that describe the nature of the exceptional situation. We use object fields for the purpose.

Although there is no limit for the data an exception can carry, it usually does not contain more than a message and the stack trace. There is room - as defined in the class `Throwable` - for other parameters, like the exception that was causing the current one (`getCause()`), or an array of suppressed exceptions (`getSuppressed()`). They are rarely used, presumably because developers are not aware of these features and because most cases are simple and do not need these possibilities. We will have a look at these possibilities in this article so that you will not belong to the group of ignorant developers who do not use these methods only because they are not aware of them.

We have a sample application. It is a bit more than just throwing, catching, and handling an exception in the `catch` branch that lets the code to continue. That is simple and is explained in the tutorial you have read when learning to program in Java the first time.

Our sample application will be a bit more complex. We will list the files in a directory, read the lines, and count the number of `wtf` strings. This way we automate the code review process quality measurement (joking). It is said that the code quality is reverse proportional to the number of the WTFs during the code review.

The solution contains

* a `FileLister` that can list the files,
* a `FileReader` that can read a file,
* a `LineWtfCounter` that will count the `wtf`s in a single line,
* a `FileWtfCounter` that will use the previous class to count all the `wtf`s in the whole file listing the lines, and finally,
* a `ProjectWtfCounter` that counts the `wtf`s in the whole project using the file level counter, listing all the files.


### Version 1, throw and catch

The application functionality is fairly simple and because we focus on the exception handling the implementation is also trivial. For example, the file listing class is as simple as the following:

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

We have three files in the file system, `a.txt`, `b.txt`, and `c.txt`. This is a mock, of course, but in this case, we do not need anything more complex to demonstrate the exception handling. Similarly, the `FileReader` is also a kind of mock implementation that serves demonstration purposes only:

<!-- snip FileReader_v1 -->
```java
package javax0.blog.demo.throwable.v1;

import java.util.List;

public class FileReader {
    final String fileName;

    public FileReader(String fileName) {
        this.fileName = fileName;
    }

    public List<String> list() {
        if (fileName.equals("a.txt")) {
            return List.of("wtf wtf", "wtf something", "nothing");
        }
        if (fileName.equals("b.txt")) {
            return List.of("wtf wtf wtf", "wtf something wtf", "nothing wtf");
        }
        if (fileName.equals("c.txt")) {
            return List.of("wtf wtf wtf", "wtf something wtf", "nothing wtf", "");
        }
        throw new RuntimeException("File is not found: "+ fileName);
    }

}
```

The counter, which counts the number of `wtf` occurrences in a line is

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

To save space and focus on our topic the snippet does not display the actual logic (was automatically removed by Java::Geci). The reader can create a code that actually counts the number of `wtf` substrings in a string, or else simply "wtf". Even if the reader cannot write such a code it is available from the repository mentioned at the start of the article.

-----------

The logic in our application says that this is an exceptional situation if one of the lines in the file has zero length. In that case, we throw an exception.

-----------

Usually, such a situation does not verify to be an exception, and I acknowledge that this is a bit contrived example, but we needed something simple. If the length of the line is zero then we throw a `LineEmpty` exception. (We do not list the code of `LineEmpty` exception. It is in the code repo, and it is simple, nothing special. It extends `RuntimeException`, no need to declare where we throw it.) If you look at the mock implementation of `FileReader` then you can see that we planted an empty line in the file `c.txt`.

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

This is the first version of the application. It does not have any special exception handling. It just sums up the values that the line counters return and in case there is an exception on the lower level, in the line `wtf` counter then this will automatically propagate up. We do not handle that exception in any way on this level.

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
A unit test usually should not have a stack trace print. In this case we have it to demonstrate what is thrown. The stack trace in the error will show us the error as the following:

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

There is a little problem with this exception. When we use this code it does not tell us anything about the actual file and line that is problematic. We have to examine all the files and all the lines if there is an empty one. It is not too difficult to write an application for that, but we do not want to work instead of the programmer who created the application. When there is an exception we expect the exception to give us enough information to successfully tackle the situation. The application has to tell me which file and which line is faulty.

### Version 2, setting cause

To provide the information in the exception we have to gather it and insert it into the exception. This is what we do in the second version of the application.

The exception in the first version does not contain the name of the file, or the line number because the code does not put it there. The code has a good reason to do that. The code at the location of the exception throwing does not have the information and thus it cannot insert into the exception what it does not have.

A lucrative approach could be to pass this information along with the other parameters so that when an exception happens the code can insert this information into the exception. I do not recommend that approach. If you look at the source codes I published on GitHub you may find examples of this practice. I am not proud of them, and I am sorry.
Generally, I recommend that the exception handling should not interfere with the main data flow of the application. It has to be separated as it is a separate concern.

The solution is to handle the exception on several levels, on each level adding the information, which is available at the actual level. To do that we modify the classes `FileWtfCounter` and `ProjectWftCounter`.

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

The code catches the exception that signals the empty line and throws a new one, which already has a parameter: the serial number of the line.

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
* store the information in its original form as long as possible so that the use of it is not limited

The first two criteria are general. The last one is special in this case, although it is not specific to exception handling. When we are handling exceptions, however, it is very lucrative to just generate a message that contains the line number instead of complicating the structure of the exception class. After all, the reasoning that we will never
use the exception for anything else than printing it to the screen is valid. Or not? It depends. First of all, never say never. Second thought: if we encode the line number into the message then it is certain that we will not ever use it for anything else than printing it to the user. That is because we cannot use it for anything else. We limit ourselves. The today programmer limits the future programmer to do something meaningful with the data.

You may argue that [this is YAGNI](https://en.wikipedia.org/wiki/You_aren%27t_gonna_need_it). We should care about storing the line number as an integer when we want to use it and caring about it at the very moment is too early and is just a waste of time. You are right! At the same time, the person who is creating the extra field and the `getMessage()` method that calculates the text version of the exception information is also right. Sometimes there is a very thin line between YAGNI and careful and good style programming. YAGNI is to avoid complex code that later you will not need (except that when you create it, you think that you will need). In this example, I have the opinion that the above exception with that one extra `int` field is not "complex".

We have a similar code on the "project" level, where we handle all the files. The code of `ProjectWftCounter` will be

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

The exception `FileNumberedLineEmpty` is also similar to the code of `NumberedLineEmpty`. Here is the code of `FileNumberedLineEmpty`:

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

At this moment I would draw your focus to the fact that the exceptions that we created are also in inheritance hierarchy. They extend the other as the information we gather and store is extended, thus:

```
FileNumberedLineEmpty - extends -> NumberedLineEmpty - extends -> LineEmpty
```

If the code using these methods expects and tries to handle a `LineEmpty` exception then it can do even if we throw a more detailed and specialized exception. If a code wants to use the extra information then it, eventually, has to know that the actual instance is not `LineEmpty` rather something more specialized as `NumberedLineEmpty` or `FileNumberedLineEmpty`. However, if it only wants to print it out, get the message then it is absolutely fine to handle the exception as an instance of `LineEmpty`. Even doing so the message will contain the extra information in human-readable form thanks to OO programming polymorphism.

The proof of the pudding is in the eating. We can run our code with the simple test. The test code is the same as it was in the previous version  with the only exception that the expected exception type is `FileNumberedLineEmpty` instead of `LineEmpty`. The printout, however, is interesting:

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

We can be happy with this result as we immediately see that the file, which is causing the problem is `c.txt` and the fourth line is the one, which is the culprit. On the other hand, we cannot be happy when we want to have a look at the code that was throwing the exception. Sometime in the future, we may not remember why a line must not have zero length. In that case, we want to look at the code. There we will only see that an exception is caught and rethrown. Luckily there is the cause, but it is actually three steps till we get to the code that is the real problem at `LineWtfCounter.java:15`.

Will anyone ever be interested in the code that is catching and rethrowing an exception? Maybe yes. Maybe no. In our case, we decide that there will not be anyone interested in that code and instead of handling a long chain of exception listing the causation of the guilty we change the stack trace of the exception that we throw to that of the causing
exception.

### Version 3, setting the stack trace

In this version, we only change the code of the two exceptions: `NumberedLineEmpty` and `FileNumberedLineEmpty`. Now they not only extend one the other and the other one `LineEmpty` but they also set their own stack trace to the value that the causing exception was holding.

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

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
```

There is a public `setStackTrace()` method that can be used to set the stack trace of an exception. The interesting thing is that this method is really `public` and not protected. The fact that this method is `public` means that the stack trace of any exception can be set from outside. Doing that is (probably) against encapsulation rules.
Nevertheless, it is there and if it is there then we can use it to set the stack trace of the exception to be the same as it is that of the causing exception.

There is another interesting piece of code in these exception classes. This is the public `fillInStackTrace()` method. If we implement this, like the above then we can save the time the exception spends during the object construction collecting its own original stack trace that we replace and throw away anyway.

When we create a new exception the constructor calls a native method to fill in the stack trace. If you look at the default constructor of the class `java.lang.Throwable` you can see that actually this is all it does (Java 14 OpenJDK):

```java
public Throwable() {
    fillInStackTrace();
}
```

The method `fillInStackTrace()` is not native but this is the method that actually invokes the native `fillInStackTrace(int)` method that does the work. Here is how it is done:

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

There is some "magic" in it, how it sets the field `stackTrace` but that is not really important as for now. It is important, however, to note that the method `fillInStackTrace()` is `public`. This means that it can be overridden. (For that, `protected` would have been enough, but `public` is even more permitting.)

We also set the causing exception, which, in this case will have the same stack trace. Running the test (similar to the previous tests that we listed only one of), we get the stack print out:

```
javax0.blog.demo.throwable.v3.FileNumberedLineEmpty: c.txt:4 is empty
	at javax0.blog.demo.throwable.v3.LineWtfCounter.count(LineWtfCounter.java:15)
	at javax0.blog.demo.throwable.v3.FileWtfCounter.count(FileWtfCounter.java:16)
	at javax0.blog.demo.throwable.v3.ProjectWftCounter.count(ProjectWftCounter.java:19)
	at javax0.blog.demo.throwable.v3.TestWtfCounter.lambda$testThrowing$0(TestWtfCounter.java:17)
	at org.assertj.core.api.ThrowableAssert.catchThrowable(ThrowableAssert.java:62)
...
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
Caused by: javax0.blog.demo.throwable.v3.NumberedLineEmpty: line 4. has zero length
	... 71 more
Caused by: javax0.blog.demo.throwable.v3.LineEmpty: There is a zero length line
	... 71 more
```

It should be no surprise that we have a `FileNumberedLineEmpty` with a stack trace that starts on a code line `LineWtfCounter.java:15` that does not throw that exception. When we see this there can be some debate about:

* Why do we need the causing exceptions attached to the original when we overwrite the stack trace? (We do not.)
* Is this a clean solution? It may be confusing that the stack trace originates from a line that does not throw that exception. 

Let's answer these concerns with, yes, they are needed for the demonstration purpose, and in a real application every programmer may decide if they want to use a solution like that.

Is this the best solution we can get? Probably no, because, as I promised, we have a fourth version of the application.

### Version 4, suppressing exceptions

When we created the mock `FileReader` we were optimistic a lot. We assumed that there is only one line that has zero length. What if there are more than one lines like that? In that case, the application stops at the first one. The user fixes the error either adding some characters to the line, so that this is not an empty one, or deleting it altogether so that this is not a line anymore. Then the user runs the application again to get the second location in the exception. If there are many such lines to correct then this process can be cumbersome. You can also imagine that the code in a real application may run for long minutes let alone for hours. To execute the application just to get the next location of the problem is a waste of human time, waste of CPU clock, energy, and thus clean oxygen generating CO2 unnecessarily.

What we can do is, alter the application so that it goes on processing when there is an empty line, and it throws an exception listing all the lines that were empty and discovered during the process only after all the files and all the lines were processed. There are two ways. One is to create some data structure and store the information in there and at the end of the processing, the application can have a look at that and throw an exception if there is any information about some empty lines there. The other one is to use the structures provided by the exception classes to store the information.

The advantage is to use the structures provided by the exception classes are

* the structure is already there and there is no need to reinvent the wheel,

* it is well-designed by many seasoned developers and used for decades, probably is the right structure,

* the structure is general enough to accommodate other types of exceptions, not only those that we have currently, and the data structure does not need any change.

Let's discuss the last bullet point a bit. It may happen that later we decide that lines that contain `WTF` all capital are also exceptional and should throw an exception. In that case, we may need to modify our data structures that store these error cases if we decided to craft these structures by hand. If we use the suppressed exceptions of the Throwable class then there is nothing extra to do. There is an exception, we catch it (as you will see in the example soon), store it, and then attach it at the end of the summary exception as a suppressed exception. Is it YAGNI that we think about this future possibility when it is extremely unlikely that this demo application will ever be extended? Yes, and no, and generally it does not matter. YAGNI is usually a problem when you devote time and effort to develop something too early. It is an extra cost in the development and later in the maintenance. When we are just using something simpler that is already there then it is not YAGNI to use it. It is simply clever and knowledgable about the tool we use.

Let's have a look at the modified `FileReader` that this time already returns many empty lines in many files:

<!-- snip FileReader_v4 -->
```java
package javax0.blog.demo.throwable.v4;

import java.io.FileNotFoundException;
import java.util.List;

public class FileReader {
    final String fileName;

    public FileReader(String fileName) {
        this.fileName = fileName;
    }

    public List<String> list() {
        if (fileName.equals("a.txt")) {
            return List.of("wtf wtf", "wtf something", "", "nothing");
        }
        if (fileName.equals("b.txt")) {
            return List.of("wtf wtf wtf", "", "wtf something wtf", "nothing wtf", "");
        }
        if (fileName.equals("c.txt")) {
            return List.of("wtf wtf wtf", "", "wtf something wtf", "nothing wtf", "");
        }
        throw new RuntimeException("File is not found: "+ fileName);
    }

}
```

Now all three files contain lines that are empty. We do not need to modify the `LineWtfCounter` counter. When there is an empty line, we throw an exception. On this level, there is no way to suppress this exception. We cannot collect here any exception list. We focus on one single line that may be empty.

The case is different in `FileWtfCounter`:

<!-- snip FileWtfCounter_v4 -->
```java
package javax0.blog.demo.throwable.v4;

public class FileWtfCounter {
    private final FileReader fileReader;

    public FileWtfCounter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public int count() {
        final var lines = fileReader.list();
        NumberedLinesAreEmpty exceptionCollector = null;
        int sum = 0;
        int lineNr = 1;
        for (final var line : lines) {
            try {
                sum += new LineWtfCounter(line).count();
            }catch(LineEmpty le){
                final var nle = new NumberedLineEmpty(lineNr,le);
                if( exceptionCollector == null ){
                    exceptionCollector = new NumberedLinesAreEmpty();
                }
                exceptionCollector.addSuppressed(nle);
            }
            lineNr ++;
        }
        if( exceptionCollector != null ){
            throw exceptionCollector;
        }
        return sum;
    }

}
```

When we catch a `LineEmpty` exception we store it in an aggregate exception referenced by the local variable `exceptionCollector`. If there is not `exceptionCollector` then we create one before adding the caught exception to it to avoid NPE. At the end of the processing when we processed all the lines we may have many exceptions added to the summary exception `exceptionCollector`. If it exists then we throw this one.

Similarly, the `ProjectWftCounter` collects all the exceptions that are thrown by the different `FileWtfCounter` instances and at the end of the processing it throws the summary exception as you can see in the following code lines:

<!-- snip ProjectWftCounter_v4 -->
```java
package javax0.blog.demo.throwable.v4;

import javax0.blog.demo.throwable.FileLister;

public class ProjectWftCounter {

    private final FileLister fileLister;

    public ProjectWftCounter(FileLister fileLister) {
        this.fileLister = fileLister;
    }


    public int count() {
        final var fileNames = fileLister.list();
        FileNumberedLinesAreEmpty exceptionCollector = null;
        int sum = 0;
        for (final var fileName : fileNames) {
            try {
                sum += new FileWtfCounter(new FileReader(fileName)).count();
            } catch (NumberedLinesAreEmpty nle) {
                if( exceptionCollector == null ){
                    exceptionCollector = new FileNumberedLinesAreEmpty();
                }
                exceptionCollector.addSuppressed(nle);
            }
        }
        if( exceptionCollector != null ){
            throw exceptionCollector;
        }
        return sum;
    }
}
```

Now that we have collected all the problematic lines into a huge exception structure we get a stack trace that we deserve:

```text
javax0.blog.demo.throwable.v4.FileNumberedLinesAreEmpty: There are empty lines
	at javax0.blog.demo.throwable.v4.ProjectWftCounter.count(ProjectWftCounter.java:24)
	at javax0.blog.demo.throwable.v4.TestWtfCounter.lambda$testThrowing$0(TestWtfCounter.java:17)
	at org.assertj.core.api.ThrowableAssert.catchThrowable(ThrowableAssert.java:62)
	at org.assertj.core.api.AssertionsForClassTypes.catchThrowable(AssertionsForClassTypes.java:750)
	at org.assertj.core.api.Assertions.catchThrowable(Assertions.java:1179)
	at javax0.blog.demo.throwable.v4.TestWtfCounter.testThrowing(TestWtfCounter.java:15)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:686)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:140)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestMethod(TimeoutExtension.java:84)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:205)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:201)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:137)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:71)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1510)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1510)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:248)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$5(DefaultLauncher.java:211)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:226)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:199)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:132)
	at com.intellij.junit5.JUnit5IdeaTestRunner.startRunnerWithArgs(JUnit5IdeaTestRunner.java:69)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:230)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:58)
	Suppressed: javax0.blog.demo.throwable.v4.NumberedLinesAreEmpty
		at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:22)
		at javax0.blog.demo.throwable.v4.ProjectWftCounter.count(ProjectWftCounter.java:21)
		... 68 more
		Suppressed: javax0.blog.demo.throwable.v4.NumberedLineEmpty: line 3.
			at javax0.blog.demo.throwable.v4.LineWtfCounter.count(LineWtfCounter.java:15)
			at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:18)
			... 69 more
		Caused by: javax0.blog.demo.throwable.v4.LineEmpty: There is a zero length line
	Suppressed: javax0.blog.demo.throwable.v4.NumberedLinesAreEmpty
		at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:22)
		at javax0.blog.demo.throwable.v4.ProjectWftCounter.count(ProjectWftCounter.java:21)
		... 68 more
		Suppressed: javax0.blog.demo.throwable.v4.NumberedLineEmpty: line 2.
			at javax0.blog.demo.throwable.v4.LineWtfCounter.count(LineWtfCounter.java:15)
			at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:18)
			... 69 more
		Caused by: javax0.blog.demo.throwable.v4.LineEmpty: There is a zero length line
		Suppressed: javax0.blog.demo.throwable.v4.NumberedLineEmpty: line 5.
			at javax0.blog.demo.throwable.v4.LineWtfCounter.count(LineWtfCounter.java:15)
			at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:18)
			... 69 more
		Caused by: javax0.blog.demo.throwable.v4.LineEmpty: There is a zero length line
	Suppressed: javax0.blog.demo.throwable.v4.NumberedLinesAreEmpty
		at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:22)
		at javax0.blog.demo.throwable.v4.ProjectWftCounter.count(ProjectWftCounter.java:21)
		... 68 more
		Suppressed: javax0.blog.demo.throwable.v4.NumberedLineEmpty: line 2.
			at javax0.blog.demo.throwable.v4.LineWtfCounter.count(LineWtfCounter.java:15)
			at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:18)
			... 69 more
		Caused by: javax0.blog.demo.throwable.v4.LineEmpty: There is a zero length line
		Suppressed: javax0.blog.demo.throwable.v4.NumberedLineEmpty: line 5.
			at javax0.blog.demo.throwable.v4.LineWtfCounter.count(LineWtfCounter.java:15)
			at javax0.blog.demo.throwable.v4.FileWtfCounter.count(FileWtfCounter.java:18)
			... 69 more
		Caused by: javax0.blog.demo.throwable.v4.LineEmpty: There is a zero length line
```

This time I did not delete any line to make you feel the weight of it on your shoulder. Now you may start to think if it was really worth using the exception structure instead of some neat, slim special-purpose data structure that contains only the very information that we need. If you start to think that, [then stop it](https://youtube.com/watch?v=Ow0lr63y4Mw). Don't do it. The problem, if any, is not that we have too much information. The problem is the way we represent it. To overcome it the solution is not to throw out the baby with the bathwater... the excess information but rather to represent it in a more readable way. If the application rarely meets many empty lines, then reading through the stack trace may not be an unbearable burden for the user. If it is a frequent problem, and you want to be nice to your users (customers, who pay your bills) then, perhaps, a nice exception structure printer is a nice solution.

We actually have one for you in the project

`javax0.blog.demo.throwable.v4.ExceptionStructurePrettyPrinter`

that you can use and even modify at your will. With this the printout of the previous "horrendous" stack trace will print out as:

```text
FileNumberedLinesAreEmpty("There are empty lines")
    Suppressed: NumberedLineEmpty("line 3.")
      Caused by:LineEmpty("There is a zero length line")
    Suppressed: NumberedLineEmpty("line 2.")
      Caused by:LineEmpty("There is a zero length line")
    Suppressed: NumberedLineEmpty("line 5.")
      Caused by:LineEmpty("There is a zero length line")
    Suppressed: NumberedLineEmpty("line 2.")
      Caused by:LineEmpty("There is a zero length line")
    Suppressed: NumberedLineEmpty("line 5.")
      Caused by:LineEmpty("There is a zero length line")
``` 

With this, we got to the end of the exercise. We stepped through the steps from `v1` simply throwing and catching and exception, `v2` setting causing exceptions matryoshka style, `v3` altering the stack trace of the embedding exception, and finally `v4` storing all the suppressed exceptions that we collected during our process. What you can do now is download the project, play around with it, examine the stack traces, modify the code, and so on. Or read on, we have some extra info about exceptions that are rarely discussed by basic level tutorials, and it is also worth reading the final takeaway section.

## Other things to know about exceptions

In this section, we will tell you some information that is not well known and is usually missing from the basic Java tutorials that talk about exceptions.

### There is no such thing as checked exception in the JVM

Checked exceptions cannot be thrown from a Java method unless the method declaration explicitly says that this may happen. The interesting thing is that the notion of checked exceptions is not known for the JVM. This is something handled by the Java compiler, but when the code gets into the JVM there is no check about that.

```text
Throwable (checked) <-- Exception (checked) <-- RuntimeException (unchecked)
                                            <-- Other Exceptions (checked)
                    <-- Error (unchecked)
```

The structure of the exception classes is as described above. The root class for the exceptions is the `Throwable`. Any object that is an instance of a class, which extends directly or indirectly the `Throwable` class can be thrown. The root class `Throwable` is checked, thus if an instance of it is thrown from a method, then it has to be declared.
If any class extends this class directly and is thrown from a method then, again it has to be declared. Except if the object is also an instance of `RuntimeException` or `Error`. In that case the exception or error is not checked and can be thrown without declaring on the throwing method.

The idea of checked exception is controversial. There are advantages of its use but there are many languages that do not have the notion of it. This is the reason why the JVM does not enforce the declaration of checked exceptions. If it did it would not be possible reasonably to generate JVM code from languages that do not require exceptions declared and want to interoperate with the Java exceptions. Checked exceptions also cause a lot of headaches when we are using streams in Java.

It is possible to overcome of checked exceptions. A method created with some hack, or simply in a JVM language other than Java can throw a checked exception even if the method does not declare the exception to be thrown. The hacky way uses a simple `static` utility method, as listed in the following code snippet:

<!-- snip SneakyThrower-->
```java
package javax0.blog.demo.throwable.sneaky;

public class SneakyThrower {
    public static <E extends Throwable> E throwSneaky(Throwable e) throws E {
        throw (E) e;
    }
}
```

When a code throws a checked exception, for example `Exception` then passing it to `throwSneaky()` will fool the compiler. The compiler will look at the declaration of the static method and cannot decide if the `Throwable` it throws is checked or not. That way it will not require the declaration of the exception in the throwing method.

The use of this method is very simple and is demonstrated with the following unit test code:

<!-- snip SneakyThrowTest-->
```java
package javax0.blog.demo.throwable.sneaky;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javax0.blog.demo.throwable.sneaky.SneakyThrower.throwSneaky;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestSneaky {

    @DisplayName("Can throw checked exception without declaring it")
    @Test
    void canThrowChecked() {
        class FlameThrower {
            void throwExceptionDeclared() throws Exception {
                throw new Exception();
            }

            void throwExceptionSecretly() {
                throwSneaky(new Exception());
            }
        }
        final var sut = new FlameThrower();
        assertThat(catchThrowable(() -> sut.throwExceptionDeclared())).isInstanceOf(Exception.class);
        assertThat(catchThrowable(() -> sut.throwExceptionSecretly())).isInstanceOf(Exception.class);
    }

    int doesNotReturn() {
        throw throwSneaky(new Exception());
        // no need for a return command
    }

}
```

The two methods `throwExceptionDeclared()` and `throwExceptionSecretly()` demonstrate the difference between normal and sneaky throwing.

The method `throwSneaky()` never returns, and it still has a declared return value. The reason for that is to allow the pattern that can be seen in the method `doesNotReturn()` towards the end of the text code. We know that the method `throwSneaky()` never returns, but the compiler does not know. If we simply call it then the compiler will still require some return statement in our method. In more complex code flow it may complain about uninitialized variables. On the other hand if we "throw" the return value in the code then it gives the compiler a hint about the execution flow. The actual throwing on this level will never happen actually, but it does not matter.

### Never catch `Throwable`, `...Error` or `COVID`

When we catch an exception we can catch checked exception, `RuntimeException` or just anything that is `Throwable`. However, there are other things that are `Throwable` but are not exceptions and are also not checked. These are errors.

Story:

I do a lot of technical interviews where candidates come and answer my questions. I have a lot of reservations and bad feelings about this. I do not like to play "God". On the other hand, I enjoy a lot when I meet clever people, even if they are not fit for a given work position. I usually try to conduct the interviews that the value from it is not only the evaluation of the candidate but also something that the candidate can learn about Java, the profession, or just about themselves. There is a coding task that can be solved using a loop, but it lures inexperienced developers to have a solution that is recursive. Many of the developers who create the recursive solution realize that there is no exit condition in their code for some type of the input parameters. (Unless there is because they do it in the clever way. However, when they are experienced enough, they do not go for the recursive solution instead of a simple loop. So when it is a recursive solution they almost never have an exit condition.) What will happen if we run that code with an input parameter that never ends the recursive loop? We get a `StackOverflowException`. Under the pressure and stress of the interview, many of them craft some code that catches this exception. This is problematic. This is a trap!

Why is it a trap? Because the code will not ever throw a `StackOverflowException`. There is no such thing in the JDK as `StackOverflowException`. It is `StackOverflowError`. It is not an exception, and the rule is that

&gt;YOUR CODE MUST NEVER CATCH AN ERROR

The `StackOverflowError` (not exception) extends the class `VirtualMachineError` which says in the JavaDoc:

&gt;Thrown to indicate that the Java Virtual Machine is broken

When something is broken you can glue it together, mend, fix, but you can never make it unbroken. If you catch a `Throwable` which is also an instance of `Error` then the code executing in the `catch` part is run in a broken VM. What can happen there? Anything and the continuation of the execution may not be reliable.

Never catch an `Error`!

## Summary and Takeaway

In this article we discussed exceptions, specifically:

- how to throw meaningful exceptions by adding information when it becomes available, 

- how to replace the stack trace of an exception with `setTrackTrace()` when it makes sense,

- how to collect exceptions with `addSuppressed()` when your application can throw exceptions multiple times We also discussed some interesting bits about how the JVM does not know about checked exceptions and why you should never catch an `Error`.

Don't just (re)throw exceptions when they happen. Think about why and how they happen and handle them appropriately.

Use the information in this article to make your code exceptional ;-)

(Code and article were reviewed and proofread by Mihaly Verhas. He also wrote the takeaway section including the last
sentence.)
