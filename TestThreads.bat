@echo This batch file is deprecated; for an equivalent try:
@echo set JAVA_OPTS=-mx64M
@echo build threads 
@goto end

@echo off
@goto start
@REM	Name:   TestThreads.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM    Simple wrapper for TestThreads and TestMultiTypeThreads

:start
if "%1" == "" set TEST_NAME=TestMultiTypeThreads
if not "%1" == "" set TEST_NAME=%1
set END_PKG=trax
if not "%JAVA_OPTS%" == "" set JAVA_OPTS_SAVE=%JAVA_OPTS%
@REM for this test, we want to test thread saftey, not performance, 
@REM    so we might as well ask for a bunch of memory
set JAVA_OPTS=-mx64M %JAVA_OPTS%
@echo Wrapper using 'trax.%TEST_NAME% -load TestThreads.properties'
call runtest.bat %TEST_NAME% -load TestThreads.properties %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
set TEST_NAME=
set JAVA_OPTS=
if not "%JAVA_OPTS_SAVE%" == "" set JAVA_OPTS=%JAVA_OPTS_SAVE%
:end
