@echo off
@goto start
@REM	Name:   perf.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run StylesheetTestletDriver over tests\perf
@REM See build.bat/.xml for how to pass -Dqetest.foo=bar options

:start
@echo Currently defaulting JAVA_OPTS to include -mx64m for performance measurements
set SJAVA_OPTS=%JAVA_OPTS%
set JAVA_OPTS=-mx64m %JAVA_OPTS%

@echo Redirect to build.bat perf %1 %2 %3 %4 %5 %6 %7 %8 %9
call build.bat %DASHCRIMSON% perf %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
set JAVA_OPTS=%SJAVA_OPTS%
