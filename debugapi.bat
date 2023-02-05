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
@REM	Name:   debugapi.bat
@REM	Author: Shane_Curcuru@us.ibm.com
@REM Wrapper for running tests without Ant in a debugger
@REM Usage: debugapi.bat org.apache.qetest.package.TestName [other options]

rem     Setup:
rem        - you must set JAVA_HOME environment variable

:start
set _JAVACMD=%JAVA_HOME%\bin\java

set XALAN_BUILD_DIR_PATH=..\xalan-java\build;..\build

set XERCES_ENDORSED_DIR_PATH=..\xalan-java\lib\endorsed;..\lib\endorsed

set APICP=java\build\testxsl.jar;tools\jtidy-r938.jar
@echo Executing: java -Djava.endorsed.dirs=%XALAN_BUILD_DIR_PATH%;%XERCES_ENDORSED_DIR_PATH% -classpath %APICP% %1 -load debugapi.properties %2 %3 %4 %5 %6 %7 %8 %9
"%_JAVACMD%" -Djava.endorsed.dirs=%XALAN_BUILD_DIR_PATH%;%XERCES_ENDORSED_DIR_PATH% -classpath %APICP% %1 -load debugapi.properties %2 %3 %4 %5 %6 %7 %8 %9
set APICP=
:end
