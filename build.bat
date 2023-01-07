@echo off
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
rem     Author: shane_curcuru@lotus.com,
                ggregory@apache.org,
                mukulg@apache.org
                
rem     See:    build.xml

rem     Setup:
rem        - you must set JAVA_HOME environment variable
rem        - you can set ANT_HOME environment variable if you use your own Ant install

echo.
echo Xalan-J test automation build
echo -----------------------------

if "%JAVA_HOME%"=="" goto noJavaHome

if exist "%JAVA_HOME%\lib\tools.jar" (
   set _CLASSPATH=%JAVA_HOME%\lib\tools.jar
)

set _JAVACMD=%JAVA_HOME%\bin\java

rem On windows grab all arguments at once
set ANT_CMD_LINE_ARGS=%*

rem Default ANT_HOME to the one what user has set
if not "%ANT_HOME%"=="" set _ANT_HOME=%ANT_HOME%
if "%ANT_HOME%"=="" set _ANT_HOME=..\xalan-java

if exist "%_ANT_HOME%\tools\ant.jar" (
   set _ANT_JARS=%_ANT_HOME%\tools\ant.jar
) else if EXIST "%_ANT_HOME%\..\tools\ant.jar" (
   set _ANT_JARS=%_ANT_HOME%\..\tools\ant.jar
) else (
   set _ANT_JARS=%_ANT_HOME%\lib\ant.jar;%_ANT_HOME%\lib\ant-launcher.jar
)

set _CLASSPATH=%_CLASSPATH%;%_ANT_JARS%

@echo on
"%_JAVACMD%" -mx1024m -classpath "%_CLASSPATH%" org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%
@echo off

goto end

:noJavaHome
echo Warning: JAVA_HOME environment variable is not set

:end
set _CLASSPATH=
set _ANT_HOME=
set _JAVACMD=
set _ANT_JARS=