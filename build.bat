@echo off
@goto start
rem     Name:   test.bat
rem     Author: shane_curcuru@lotus.com
rem     See:    test.xml
:usage
@echo test.bat - executes Xalan Java-based test automation
@echo   Usage:   test [target] [-D options]
@echo   Example: test api -DtestClass=TransformerAPITest -Dqetest.loggingLevel
@echo.
@echo   EITHER: set environment variable JARDIR to point to dir 
@echo   containing *all* needed .jars to run, ...
@echo   ... OR: be in xml-xalan/test having built whatever .jars you 
@echo   need in the normal locations, ...
@echo   ... OR: pass appropriate ANT_OPTS or the like to reset .jar 
@echo   file locations to your locations
@echo.
@echo   You should have JAVA_HOME/lib/tools.jar, etc. in your CLASSPATH
@echo   You may set PARSER_JAR to specific path/filename.jar of parser
@echo   You may set JAVA_OPTS to be passed to java program
@echo   All other command line opts are passed to Ant
@echo.
goto done
rem ------------------------------------------------------------------------
rem Blatantly modeled on ant.bat
:start
rem Check for help requests
if '%1' == '' goto usage
if '%1' == '-h' goto usage
if '%1' == '-H' goto usage
if '%1' == '-?' goto usage
@echo %0 beginning...

if not "%OS%"=="Windows_NT" goto win9xStart
:winNTStart
@setlocal

rem On NT/2K grab all arguments at once
set ANT_CMD_LINE_ARGS=%*
goto doneStart

:win9xStart
rem Slurp the command line arguments.  This loop allows for an unlimited number of 
rem agruments (up to the command line limit, anyway).

set ANT_CMD_LINE_ARGS=

:setupArgs
if "%1" == "" goto doneStart
set ANT_CMD_LINE_ARGS=%ANT_CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart
rem This label provides a place for the argument list loop to break out 
rem and for NT handling to skip to.

rem default ANT_HOME
if "%ANT_HOME%"=="" set ANT_HOME=java
goto checkJava

:checkJava
set _JAVACMD=%JAVACMD%
set _ANT_CP=%CLASSPATH%
rem Note: classpath handling is special for testing Xalan
rem If PARSER_JAR blank, default to xerces in the xalan dir
if "%PARSER_JAR%" == "" set _PARSER_JAR=..\java\bin\xerces.jar
if not "%PARSER_JAR%" == "" set _PARSER_JAR=%PARSER_JAR%

rem If JARDIR is blank, then only add Ant and a PARSER_JAR to the 
rem    classpath before running Ant - then within the Ant file, it 
rem    will add other .jars from default locations
if "%JARDIR%" == "" set _ANT_CP=%CLASSPATH%;%ANT_HOME%\bin\ant.jar;%_PARSER_JAR%

rem Else if JARDIR is set, then put all Xalan-J 2.x required .jar files 
rem    in the classpath first from that one dir
rem Note: Does not yet support xsltc testing! TBD -sc
if not "%JARDIR%" == "" set _ANT_CP=%JARDIR%\xerces.jar;%JARDIR%\xalan.jar;%JARDIR%\bsf.jar;%JARDIR%\js.jar;%_ANT_HOME%\bin\ant.jar;%CLASSPATH%

if "%JAVA_HOME%" == "" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java
goto runAnt

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java
echo.
echo Warning: you should set JAVA_HOME and add tools.jar/classes.zip to your CLASSPATH!.
echo.

:checkJikes
if not "%JIKESPATH%" == "" goto runAntWithJikes

:runAnt
%_JAVACMD% -classpath %_ANT_CP% -Dant.home="%ANT_HOME%" %ANT_OPTS% org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%
goto end

:runAntWithJikes
%_JAVACMD% -classpath %_ANT_CP% -Dant.home="%ANT_HOME%" -Djikes.class.path=%JIKESPATH% %ANT_OPTS% org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%

:end
set _ANT_CP=
set _JAVACMD=
set _PARSER_JAR=
set ANT_CMD_LINE_ARGS=

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
@echo %0 completed!

