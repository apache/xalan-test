@echo off
@goto start
@REM	Name:   threads.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run ThreadedTestletDriver with 
@REM    threads.properties and threads.filelist
@REM See build.bat/.xml for how to pass -Dqetest.foo=bar options

:start
@echo Redirect to build.bat threads %1 %2 %3 %4 %5 %6 %7 %8 %9
call build.bat %DASHCRIMSON% threads %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
