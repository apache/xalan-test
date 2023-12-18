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
rem             jkesselm@apache.org
rem                
rem     See:    build.xml
rem
rem     Setup:
rem        1) You must set JAVA_HOME
rem
rem        2) You can set ANT_HOME if you use your own Ant install

echo.
echo Xalan-J test automation build
echo -----------------------------

if "%JAVA_HOME%"=="" goto noJavaHome

if exist "%JAVA_HOME%\lib\tools.jar" (
   set _CLASSPATH=%JAVA_HOME%\lib\tools.jar
)

set _JAVACMD=%JAVA_HOME%\bin\java

rem On windows grab all arguments at once
set _ANT_CMD_LINE_ARGS=%*

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

set _XALAN_BUILD_CLASSPATH=..\xalan-java\build\*;..\build\*
set _XERCES_ENDORSED_CLASSPATH=..\xalan-java\lib;..\lib;..\xalan-java\lib\endorsed;..\lib\endorsed
set _XERCES_IMPL_CLASSPATH=..\xalan-java\lib\*;..\lib\*;..\xalan-java\lib\endorsed\*;..\lib\endorsed\*

: Override JRE defaults to set our own, preferring the "real" Apache code
: to the shadowed version that ships with the JRE.
set _JAXP_USE_APACHE="-Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl"

: Endorsed should no longer be necessary, given JAXP/TrAX overrides above.
: Just make sure they're on the classpaths.
: set _USE_OLD_ENDORSED_DIRS=-Djava.endorsed.dirs=%_XALAN_BUILD_CLASSPATH%;%_XERCES_ENDORSED_CLASSPATH%
set _USE_OLD_ENDORSED_DIRS=
set _CLASSPATH=%_XALAN_BUILD_CLASSPATH%;%_XERCES_IMPL_CLASSPATH%;%_CLASSPATH%

@echo on
"%_JAVACMD%" -mx1024m %_USE_OLD_ENDORSED_DIRS% -classpath "%_CLASSPATH%" org.apache.tools.ant.Main %_ANT_CMD_LINE_ARGS%
@echo off

goto end

:noJavaHome
echo Warning: JAVA_HOME environment variable is not set

:end
set _CLASSPATH=
set _ANT_HOME=
set _ANT_CMD_LINE_ARGS=
set _JAVACMD=
set _ANT_JARS=
set _XALAN_BUILD_CLASSPATH=
set _XERCES_ENDORSED_CLASSPATH=
set _XERCES_IMPL_CLASSPATH=
set _JAXP_USE_APACHE=
set _USE_OLD_ENDORSED_DIRS=
