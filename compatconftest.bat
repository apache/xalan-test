@echo Xalan-J 1.x tests are deprecated, and will be removed soon!
@echo If you still have need of them, contact me about volunteering to maintain them.
@goto end
@echo off
@goto start
@REM	Name:   compatconftest.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run the ConformanceTest using
@REM    the Xalan-J 2.x backwards 1.x compatibility layer
@REM Forces use of -flavor xalan, obviously

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=xsl
@REM EXTRA_CP is used to add xalanj1compat.jar to the start of cp
if "%JARDIR%" == "" set EXTRA_CP=..\java\build\xalanj1compat.jar
if not "%JARDIR%" == "" set EXTRA_CP=%JARDIR%\xalanj1compat.jar
@echo Wrapper using '%DASHCRIMSON% xsl.ConformanceTest -load ConformanceTest.properties -flavor xalan'
call runtest.bat %DASHCRIMSON% ConformanceTest -load ConformanceTest.properties -flavor xalan %1 %2 %3 %4 %5 %6 %7 %8 %9
set EXTRA_CP=
set END_PKG=
@REM Automatically attempt to style the results into HTML
@REM Note: this is optional, but frequent enough to just try it here
@REM Call with no args to get default processing; note that if 
@REM  you've overridden logFile, this may not work
call viewResults.bat %DASHCRIMSON% results-conf\ConformanceTest.xml results-conf\ConformanceTest.html

set DASHCRIMSON=
:end
