@echo off
@goto start
rem
rem ==========================================================================
rem = Copyright 2000-2004 The Apache Software Foundation.
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
@REM	Name:   viewResults.bat
@REM	Author: Shane_Curcuru@lotus.com
:usage
@REM ------------------------------------------------------------------------
:usage
@echo viewResults.bat - transforms a result.xml into results.html
@echo   Usage: results results\MyTest.xml [MyResults.html [options]]
@echo.
@echo   NOTE:  See runtest.bat for how to set CLASSPATH or JARDIR
goto done
@REM ------------------------------------------------------------------------

:start
@REM must have name of a results.xml file
if "%1" == "" goto usage
if "%1" == "-h" goto usage
if "%1" == "-H" goto usage
if "%1" == "-?" goto usage
@echo %0 beginning...

@REM -crimson: Use crimson.jar instead of xerces.jar
@REM    shift to get rid of -crimson arg; just pass rest of args along
@REM FutureWork: for maintenance, consider dropping crimson support
set SAVED_JAVA_OPTS=%JAVA_OPTS%
if '%1' == '-crimson' set JAVA_OPTS=-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.crimson.jaxp.DocumentBuilderFactoryImpl -Dorg.xml.sax.driver=org.apache.crimson.jaxp.SAXParserFactoryImpl %JAVA_OPTS%
if '%1' == '-crimson' set PARSER_JAR=crimson.jar
if '%1' == '-crimson' shift

@REM Non-jview: Trickery to guess appropriate location of java.exe program
if "%JAVA_HOME%" == "" set JAVA_EXE=java
if not "%JAVA_HOME%" == "" set JAVA_EXE=%JAVA_HOME%\bin\java

:dojardir
@REM If PARSER_JAR blank, default to xerces
if "%PARSER_JAR%" == "" set PARSER_JAR=xercesImpl.jar

@REM If JARDIR is blank, assume default Xalan-J 2.x locations
@REM Note also that this assumes that crimson.jar is co-located 
@REM    with the xerces.jar checked into Xalan-J 2.x
@REM Note also that this assumes that js.jar is in the directory 
@REM    above xml-xalan, for lack of a better place
if "%JARDIR%" == "" echo NOTE! JARDIR is not set, defaulting to Xalan-J 2.x!
if "%JARDIR%" == "" set TEST_CP=java\build\testxsl.jar;..\java\bin\%PARSER_JAR%;..\java\bin\xml-apis.jar;..\java\build\xalan.jar;..\java\bin\bsf.jar;..\..\js.jar;%CLASSPATH%

@REM If JARDIR set, put those references first then default classpath
if not "%JARDIR%" == "" set TEST_CP=%JARDIR%\testxsl.jar;%JARDIR%\%PARSER_JAR%;%JARDIR%\xml-apis.jar;%JARDIR%\xalan.jar;%JARDIR%\bsf.jar;%JARDIR%\js.jar;%CLASSPATH%

@REM Set our output filename
if "%2" == "" set _OUTNAME=results.html
if not "%2" == "" set _OUTNAME=%2
set _XSLNAME=%RESULTSCANNER%
if "%_XSLNAME%" == "" set _XSLNAME=FailScanner.xsl
set _PARAMS=

:getparams
set _PARAMS=%_PARAMS% %3
shift /3
if "%3"=="" goto execute
goto getparams

:execute
echo "%JAVA_EXE%" %JAVA_OPTS% -classpath "%TEST_CP%" org.apache.xalan.xslt.Process -in "%1" -xsl "%_XSLNAME%" -out "%_OUTNAME%" %_PARAMS%
"%JAVA_EXE%" %JAVA_OPTS% -classpath "%TEST_CP%" org.apache.xalan.xslt.Process -in "%1" -xsl "%_XSLNAME%" -out "%_OUTNAME%" %_PARAMS%

:done
@echo %0 complete!
set TEST_CP=
set JAVA_EXE=
set _OUTNAME=
set _PARAMS=
set _XSLNAME=
set PARSER_JAR=
set JAVA_OPTS=%SAVED_JAVA_OPTS%
:end
