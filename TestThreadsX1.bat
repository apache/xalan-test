@echo Xalan-J 1.x tests are deprecated, and will be removed soon!
@echo If you still have need of them, contact me about volunteering to maintain them.
@goto end

@echo off
@goto start
@REM	Name:   TestThreadsX1.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM    Simple wrapper for TestThreads with Xalan-J 1.x API's

:start
if "%1" == "" set TEST_NAME=TestThreads
if not "%1" == "" set TEST_NAME=%1
set END_PKG=xalanj1
if not "%JAVA_OPTS%" == "" set JAVA_OPTS_SAVE=%JAVA_OPTS%
@REM for this test, we want to test thread saftey, not performance, 
@REM    so we might as well ask for a bunch of memory
set JAVA_OPTS=-mx64M %JAVA_OPTS%
@REM EXTRA_CP is used to add xalanj1compat.jar to the start of cp
if "%JARDIR%" == "" set EXTRA_CP=..\java\build\xalanj1compat.jar
if not "%JARDIR%" == "" set EXTRA_CP=%JARDIR%\xalanj1compat.jar
@echo Wrapper using 'xalanj1.%TEST_NAME% -load TestThreads.properties'
call runtest.bat %TEST_NAME% -load TestThreads.properties %2 %3 %4 %5 %6 %7 %8 %9
set EXTRA_CP=
set END_PKG=
set TEST_NAME=
set JAVA_OPTS=
if not "%JAVA_OPTS_SAVE%" == "" set JAVA_OPTS=%JAVA_OPTS_SAVE%
:end
