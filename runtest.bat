@echo off
@goto start
@REM	Name:   runtest.bat
@REM	Author: Shane_Curcuru@lotus.com
:usage
@echo runtest.bat - runs Xalan-J test automation
@echo.
@echo Notes/prerequisites: 
@echo   Assumes you're in xml-xalan/test
@echo   JAVA_OPTS Will be passed to java.exe or jview.exe
@echo   EXTRA_CP Will be prepended to the classpath (changed Jan-01)
@echo   END_PKG Will be the subpackage name after org.apache.qetest
@echo   Special: first arg= -jview: Run Microsoft's jview instead of java
@echo   Special: first arg= -crimson: Use crimson.jar instead of xerces.jar
@echo Common args include (from the java file, ignore first two "ERROR" lines): 
@REM Call with illegal arg to force Java code to print usage()
java -classpath %CLASSPATH%;testxsl.jar;java\build\testxsl.jar;%JARDIR%\testxsl.jar org.apache.qetest.xsl.XSLProcessorTestBase -load
@echo.
goto done
@REM ------------------------------------------------------------------------

:start
@echo %0 beginning...
@REM must have an option to continue
if '%1' == '' goto usage
if '%1' == '-h' goto usage
if '%1' == '-H' goto usage
if '%1' == '-?' goto usage

@REM Process special first input arguments
@REM Note jview and crimson can't both be done: write your own batch file
if '%1' == '-jview' goto setjv
if '%1' == '-JVIEW' goto setjv

@REM -crimson: Use crimson.jar instead of xerces.jar
@REM    shift to get rid of -crimson arg; just pass rest of args along
set SAVED_JAVA_OPTS=%JAVA_OPTS%
if '%1' == '-crimson' set JAVA_OPTS=-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.crimson.jaxp.DocumentBuilderFactoryImpl -Dorg.xml.sax.driver=org.apache.crimson.jaxp.SAXParserFactoryImpl %JAVA_OPTS%
if '%1' == '-crimson' set PARSER_JAR=crimson.jar
if '%1' == '-crimson' shift

@REM Non-jview: Trickery to guess appropriate location of java.exe program
if "%JAVA_HOME%" == "" set JAVA_EXE=java
if not "%JAVA_HOME%" == "" set JAVA_EXE=%JAVA_HOME%\bin\java
set CMDCP=-classpath
goto dojardir

:setjv
@REM -jview: Run Microsoft's jview instead of java
@REM    Note: We assume user has setup environment correctly!
@REM    shift to get rid of -jview arg; just pass rest of args along
shift
set JAVA_EXE=jview
set CMDCP=/cp
goto dojardir

:dojardir
@REM If PARSER_JAR blank, default to xerces
if "%PARSER_JAR%" == "" set PARSER_JAR=xerces.jar

@REM If JARDIR is blank, assume default Xalan-J 2.x locations
@REM Note that this will probably fail miserably if you're trying 
@REM    to test Xalan-J 1.x: in that case, you must set JARDIR
@REM Note also that this assumes that crimson.jar is co-located 
@REM    with the xerces.jar checked into Xalan-J 2.x
@REM Note also that this assumes that js.jar is in the directory 
@REM    above xml-xalan, for lack of a better place
if "%JARDIR%" == "" echo NOTE! JARDIR is not set, defaulting to Xalan-J 2.x!
if "%JARDIR%" == "" set TEST_CP=java\build\testxsl.jar;..\java\bin\%PARSER_JAR%;..\java\build\xalan.jar;..\java\bin\bsf.jar;..\..\js.jar;%CLASSPATH%

@REM If JARDIR set, put those references first then default classpath
if not "%JARDIR%" == "" set TEST_CP=%JARDIR%\testxsl.jar;%JARDIR%\%PARSER_JAR%;%JARDIR%\xalan.jar;%JARDIR%\bsf.jar;%JARDIR%\js.jar;%CLASSPATH%

@REM Wrappers use EXTRA_CP to add items to our classpath; if set, prepend
@REM 19-Jan-01 sc change to prepend to make Xalan-J 2.x compatibility
@REM  testing easier, since compat.jar gets added at head of classpath
if not "%EXTRA_CP%" == "" set TEST_CP=%EXTRA_CP%;%TEST_CP%

@REM Wrappers use END_PKG to switch around the end of the 
@REM    packagename; if not set, default it to most common package
if '%END_PKG%' == '' set END_PKG=xsl

@REM Assume it's a bare classname to run with extra args
@REM Note we hackishly force in .END_PKG. here, which is not a great 
@REM    idea - we should really allow users to pass a single arg 
@REM    that's the lastpkg.ClassName
echo "%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.%END_PKG%.%1  %2 %3 %4 %5 %6 %7 %8 %9
"%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.%END_PKG%.%1  %2 %3 %4 %5 %6 %7 %8 %9

:done
@echo %0 complete!
set TEST_CP=
set JAVA_EXE=
set CMDCP=
set PARSER_JAR=
set JAVA_OPTS=%SAVED_JAVA_OPTS%
if '%END_PKG%' == 'xsl' set END_PKG=
:end
