@echo off
if "%1" == "" goto usage
@echo Simple wrapper to execute a single Bugzilla test #%1

@echo javac -classpath ..\..\..\java\build\xalan.jar;..\..\..\java\build\xml-apis.jar;..\..\..\java\bin\xerces.jar;..\..\java\build\testxsl.jar;%CLASSPATH% -d build Bugzilla%1.java
javac -classpath ..\..\..\java\build\xalan.jar;..\..\..\java\build\xml-apis.jar;..\..\..\java\bin\xerces.jar;..\..\java\build\testxsl.jar;%CLASSPATH% -d build Bugzilla%1.java

@echo java -classpath build;..\..\..\java\build\xalan.jar;..\..\..\java\build\xml-apis.jar;..\..\..\java\bin\xerces.jar;..\..\java\build\testxsl.jar;%CLASSPATH% Bugzilla%1
java -classpath build;..\..\..\java\build\xalan.jar;..\..\..\java\build\xml-apis.jar;..\..\..\java\bin\xerces.jar;..\..\java\build\testxsl.jar;%CLASSPATH% Bugzilla%1

goto end
:usage
@echo %0: compile and run a single BugzillaNNNN testlet
@echo Usage: bugzilla NNNN    (NNNN is the number of the bug)

:end