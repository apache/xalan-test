@echo off
@goto start
@REM	Name:   harness.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=xsl
@echo Harness Wrapper using '%DASHCRIMSON% xsl.XSLTestHarness Harness.properties %1 %2 %3 %4 %5 %6 %7 %8 %9'
call runtest.bat %DASHCRIMSON% XSLTestHarness Harness.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=

@REM Automatically attempt to style the results into HTML
@REM Note: this is optional, but frequent enough to just try it here
@REM Call with no args to get default processing; note that if 
@REM  you've overridden logFile, this may not work
call viewHarnessResults.bat  %DASHCRIMSON% results-api\Harness.xml results-api\Harness.html

:end
set DASHCRIMSON=

