@echo off
@goto start
@REM	Name:   PerformanceTest.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper for runtest.bat to run the 'default' 
@REM    Performance test wrapper, along with any extra args provided
@REM Note this automatically adds -mx64m to JAVA_OPTS 
@REM    since here we're more concerned with timing data 
@REM    than with memory usage data

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=xsl
@echo Adding -mx64m to JAVA_OPTS
set JAVA_OPTS=-mx64m %JAVA_OPTS%
@echo Wrapper using '%DASHCRIMSON% xsl.PerformanceTest -load PerformanceTest.properties  arg1 arg2...'
call runtest.bat %DASHCRIMSON% PerformanceTest -load PerformanceTest.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
set DASHCRIMSON=
:end
