@echo This batch file is deprecated; for an equivalent try:
@echo build api -DtestClass=NameOfAPITest -Dqetest.otherOption=value
@goto end

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
@echo   uses QetestUtils 'launcher' utility to find test class
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
if '%1' == '-crimson' set JAVA_OPTS=-Dorg.xml.sax.driver=org.apache.crimson.parser.XMLReaderImpl %JAVA_OPTS%
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
@REM Sorry for the confusion: we're trying to override too many 
@REM  things here in a batch file.  Needs work (or to use Ant instead)
set _PARSER_JAR=%PARSER_JAR%

@REM If JARDIR is blank, assume default Xalan-J 2.x locations
@REM Note that this will probably fail miserably if you're trying 
@REM    to test Xalan-J 1.x: in that case, you must set JARDIR
@REM Note also that this assumes that crimson.jar is co-located 
@REM    with the xerces.jar checked into Xalan-J 2.x
@REM Note also that this assumes that js.jar is in the directory 
@REM    above xml-xalan, for lack of a better place
@REM If PARSER_JAR blank, default to xerces for non-JARDIR case
if "%_PARSER_JAR%" == "" set _PARSER_JAR=..\java\bin\xerces.jar
@REM Set other jars by environment variables as well
if "%_XALAN_JAR%" == "" set _XALAN_JAR=..\java\build\xalan.jar
if "%_BSF_JAR%" == "" set _BSF_JAR=..\java\bin\bsf.jar
if "%_JS_JAR%" == "" set _JS_JAR=..\..\js.jar
if "%JARDIR%" == "" echo NOTE! JARDIR is not set, defaulting to Xalan-J 2.x!
@REM _Note: use relative _path_.jar names
if "%JARDIR%" == "" set TEST_CP=java\build\testxsl.jar;%_PARSER_JAR%;%_XALAN_JAR%;%_BSF_JAR%;%_JS_JAR%;%CLASSPATH%

@REM If JARDIR set, put those references first then default classpath
@REM If PARSER_JAR blank, default to xerces for JARDIR case
if "%PARSER_JAR%" == "" set PARSER_JAR=xerces.jar
@REM _Note: with exception of parser, use hardcode .jar names
if not "%JARDIR%" == "" set TEST_CP=%JARDIR%\testxsl.jar;%JARDIR%\%PARSER_JAR%;%JARDIR%\xalan.jar;%JARDIR%\bsf.jar;%JARDIR%\js.jar;%CLASSPATH%

@REM Wrappers use EXTRA_CP to add items to our classpath; if set, prepend
@REM 19-Jan-01 sc change to prepend to make Xalan-J 2.x compatibility
@REM  testing easier, since compat.jar gets added at head of classpath
if not "%EXTRA_CP%" == "" set TEST_CP=%EXTRA_CP%;%TEST_CP%

@REM Use the QetestUtils 'launcher' to run the test
echo "%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.QetestUtils %1 %2 %3 %4 %5 %6 %7 %8 %9
"%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.QetestUtils %1 %2 %3 %4 %5 %6 %7 %8 %9

:done
@echo %0 complete!
set TEST_CP=
set JAVA_EXE=
set CMDCP=
set PARSER_JAR=
set _PARSER_JAR=
set _XALAN_JAR=
set _BSF_JAR=
set _JS_JAR=
set JAVA_OPTS=%SAVED_JAVA_OPTS%
:end
