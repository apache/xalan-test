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
rem             ggregory@apache.org,
rem             mukulg@apache.org
rem                
rem     See:    build.xml
rem
rem     Setup:
rem        1) You must set JAVA_HOME
rem
rem        2) You can set ANT_HOME if you use your own Ant install

rem Upon exit, revert any environment variable changes
setlocal

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

set XALAN_BUILD_DIR_PATH=..\xalan-java\build;..\build

set XERCES_ENDORSED_DIR_PATH=..\xalan-java\lib\endorsed;..\lib\endorsed

@echo on
"%_JAVACMD%" -mx1024m -Djava.endorsed.dirs=%XALAN_BUILD_DIR_PATH%;%XERCES_ENDORSED_DIR_PATH% -classpath "%_CLASSPATH%" org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%
@echo off
set _RC=%ERRORLEVEL%

goto end

:noJavaHome
echo Warning: JAVA_HOME environment variable is not set
set _RC=-1

:end
exit /b %_RC%
