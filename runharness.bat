@echo This batch file is deprecated; for an equivalent try:
@echo build harness
@goto end

@echo off
@goto start
@REM	Name:   harness.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
@REM Pass along -crimson if it's the very first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift

@REM Use alternate Harness*.properties files for 1.x/2.x testing
set HARNESSP=Harness.properties
if '%1' == '-xalanj1' set HARNESSP=Harnessxalanj1.properties
if '%1' == '-xalanj1' shift

@REM EXTRA_CP is used to add xalanj1compat.jar to the start of cp
@REM Only actually necessary for testing backwards compatibility
@REM  layer of 2.x, but shouldn't hurt in other cases
if "%JARDIR%" == "" set EXTRA_CP=..\java\build\xalanj1compat.jar
if not "%JARDIR%" == "" set EXTRA_CP=%JARDIR%\xalanj1compat.jar

set END_PKG=xsl
@echo Harness Wrapper using '%DASHCRIMSON% xsl.XSLTestHarness %HARNESSP% %1 %2 %3 %4 %5 %6 %7 %8 %9'
call runtest.bat %DASHCRIMSON% XSLTestHarness %HARNESSP% %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=

@REM Automatically attempt to style the results into HTML
@REM Note: this is optional, but frequent enough to just try it here
@REM Call with no args to get default processing; note that if 
@REM  you've overridden logFile, this may not work
call viewHarnessResults.bat  %DASHCRIMSON% results-api\Harness.xml results-api\Harness.html

:end
set DASHCRIMSON=

