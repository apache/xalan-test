@echo off
@goto start
@REM	Name:   contribtest.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run StylesheetTestletDriver over tests\contrib
@REM See build.bat/.xml for how to pass -Dqetest.foo=bar options

:start
@echo Redirect to build.bat contrib %1 %2 %3 %4 %5 %6 %7 %8 %9
call build.bat %DASHCRIMSON% contrib %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
