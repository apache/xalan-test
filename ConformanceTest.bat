@echo This batch file is deprecated; for an equivalent try:
@echo build conf
@goto end

@echo off
@goto start
@REM	Name:   ConformanceTest.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper for runtest.bat to run the 'default' 
@REM    Conformance test wrapper, along with any extra args provided

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=xsl
@echo Wrapper using '%DASHCRIMSON% xsl.ConformanceTest -load ConformanceTest.properties %1 %2 %3 %4 %5 %6 %7 %8 %9'
call runtest.bat %DASHCRIMSON% ConformanceTest -load ConformanceTest.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=

@REM Automatically attempt to style the results into HTML
@REM Note: this is optional, but frequent enough to just try it here
@REM Call with no args to get default processing; note that if 
@REM  you've overridden logFile, this may not work
call viewResults.bat %DASHCRIMSON% results-conf\ConformanceTest.xml results-conf\ConformanceTest.html
results-conf\ConformanceTest.html

set DASHCRIMSON=
:end
