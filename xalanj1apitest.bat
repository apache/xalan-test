@echo off
@goto start
@REM	Name:   xalanj1apitest.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
set END_PKG=xalanj1
@echo Wrapper using 'xalanj1.%1 -load APITest.properties'
call runtest.bat %1 -load APITest.properties %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
:end
