@echo off
@goto start
@REM	Name:   compattest.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper for runtest.bat to run various Xalan-J 2.x 
@REM    backwards 1.x compatibility layer tests, found in 
@REM    the Xalan-J 1.x dir: org\apache\qetest\xalanj1

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=xalanj1
@REM EXTRA_CP is used to add xalanj1compat.jar to the start of cp
if "%JARDIR%" == "" set EXTRA_CP=..\java\build\xalanj1compat.jar
if not "%JARDIR%" == "" set EXTRA_CP=%JARDIR%\xalanj1compat.jar
@echo Wrapper using '%DASHCRIMSON% xalanj1.%1 -load CompatTest.properties'
call runtest.bat %DASHCRIMSON% %1 -load CompatTest.properties %2 %3 %4 %5 %6 %7 %8 %9
set EXTRA_CP=
set END_PKG=
@REM Automatically attempt to style the results into HTML
@REM Note: this is optional, but frequent enough to just try it here
@REM Call with no args to get default processing; note that if 
@REM  you've overridden logFile, this may not work
call viewResults.bat %DASHCRIMSON% results-compat\CompatTest.xml results-compat\CompatTest.html

set DASHCRIMSON=
:end
