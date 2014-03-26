@echo off
@goto start
rem
rem ==========================================================================
rem = Copyright 2001-2004 The Apache Software Foundation.
rem =
rem = Licensed under the Apache License, Version 2.0 (the "License");
rem = you may not use this file except in compliance with the License.
rem = You may obtain a copy of the License at
rem =
rem =     http://www.apache.org/licenses/LICENSE-2.0
rem =
rem = Unless required by applicable law or agreed to in writing, software
rem = distributed under the License is distributed on an "AS IS" BASIS,
rem = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem = See the License for the specific language governing permissions and
rem = limitations under the License.
rem ==========================================================================
rem
rem     Name:   build.bat
rem     Author: shane_curcuru@lotus.com
rem     See:    build.xml
:usage
@echo build.bat - compiles and executes Xalan Java-based test automation
@echo   Usage:   test [target] [-Doption=value ...]
@echo   Example: test api -DtestClass=TransformerAPITest -Dqetest.loggingLevel=99
@echo.
@echo   EITHER: set environment variable JARDIR to point to dir 
@echo   containing *all* needed .jars to run, ...
@echo   ... OR: be in xml-xalan/test having built whatever .jars you 
@echo   need in the normal locations, ...
@echo   ... OR: pass appropriate ANT_OPTS or the like to reset .jar 
@echo   file locations to your locations
@echo.
@echo   Note that even when JARDIR is set, normal .jar files may still be 
@echo     on the end of the classpath; see build.xml for details
@echo.
@echo   You should have set JAVA_HOME
@echo   You may set PARSER_JAR to specific path/filename.jar of parser
@echo     Note: PARSER_JAR is ignored when JARDIR is set
@echo   You may set JAVA_OPTS to be passed to java program
@echo   All other command line opts are passed to Ant
@echo.
@echo   build -projecthelp   will show you Ant help and build targets
@echo.

goto mainEnd
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

set classpath=..\java\tools\ant.jar;..\java\lib\endorsed\xercesImpl.jar;..\java\lib\xalan.jar;..\java\lib\serializer.jar;..\java\lib\endorsed\xml-apis.jar;%CLASSPATH%


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

rem Default ANT_HOME to the one in the java dir
if "%ANT_HOME%"=="" set _ANT_HOME=..\java
if not "%ANT_HOME%"=="" set _ANT_HOME=%ANT_HOME%

rem Patch for Ant limitation:
rem   <property environment="xxx" /> is only available on certain platforms.
rem   Apparently Windows 2003 is not one of the supported platforms.
rem   A workaround (according to Ant's FAQ) is to set the os.name=Windows_NT
rem   before calling Ant.
rem   Since this is a Windows batch file and I don't think Xalan's test 
rem   harness or targets care which Windows version we're running on, its
rem   probably safe to use this workaround.
set _ANT_OPTS=%ANT_OPTS% -Dos.name=Windows_NT

rem Note: classpath handling is special for testing Xalan
rem If PARSER_JAR blank, default to xerces in the xalan dir
if "%PARSER_JAR%" == "" set _PARSER_JAR=..\java\lib\xercesImpl.jar
if not "%PARSER_JAR%" == "" set _PARSER_JAR=%PARSER_JAR%
set _XML-APIS_JAR=%XML-APIS_JAR%
if "%_XML-APIS_JAR%" == "" set _XML-APIS_JAR=..\java\lib\xml-apis.jar

rem If JARDIR is blank, then only add Ant, PARSER_JAR, and XML-APIS_JAR to the 
rem    classpath before running Ant - then within the Ant file, it 
rem    will add other .jars from default locations
if "%JARDIR%" == "" set _CLASSPATH=%CLASSPATH%;%_ANT_HOME%\tools\ant.jar;%_XML-APIS_JAR%;%_PARSER_JAR%

rem Else if JARDIR is set, then put all Xalan-J 2.x required .jar files 
rem    in the classpath first from that one dir
rem Note: Does not yet support xsltc testing! TBD -sc
rem Note: Does not yet support using crimson from JARDIR (forces xercesImpl.jar)! TBD -sc
if not "%JARDIR%" == "" set _CLASSPATH=%JARDIR%\xml-apis.jar;%JARDIR%\xercesImpl.jar;%JARDIR%\xalan.jar;%JARDIR%\serializer.jar;%JARDIR%\testxsl.jar;%JARDIR%\bsf.jar;%JARDIR%\js.jar;%_ANT_HOME%\tools\ant.jar;%JARDIR%\Tidy.jar;%CLASSPATH%

rem Attempt to automatically add system classes to very end of _CLASSPATH
if exist "%JAVA_HOME%\lib\tools.jar" set _CLASSPATH=%_CLASSPATH%;%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set _CLASSPATH=%_CLASSPATH%;%JAVA_HOME%\lib\classes.zip


if "%JAVA_HOME%" == "" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java
goto checkJikes

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java
echo.
echo Warning: you should set JAVA_HOME and add tools.jar/classes.zip to your CLASSPATH!.
echo.

:checkJikes
rem also pass along the selected parser to Ant
rem Note: we don't need to do this for xml-apis.jar
set _ANT_OPTS=%_ANT_OPTS% -Dparserjar=%_PARSER_JAR%
if not "%JIKESPATH%" == "" goto runAntWithJikes

:runAnt
"%_JAVACMD%" %JAVA_OPTS% -classpath "%_CLASSPATH%" -Dant.home="%_ANT_HOME%" %_ANT_OPTS% org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%
goto end

:runAntWithJikes
"%_JAVACMD%" %JAVA_OPTS% -classpath "%_CLASSPATH%" -Dant.home="%_ANT_HOME%" -Djikes.class.path=%JIKESPATH% %_ANT_OPTS% org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%

:end
set _CLASSPATH=
set _ANT_HOME=
set _ANT_OPTS=
set _JAVACMD=
set _PARSER_JAR=
set _XML-APIS_JAR=
set ANT_CMD_LINE_ARGS=

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
@echo %0 completed!

