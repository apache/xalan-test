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
@echo Wrapper using '%DASHCRIMSON% xsl.ConformanceTest -load ConformanceTest.properties  arg1 arg2...'
call runtest.bat %DASHCRIMSON% ConformanceTest -load ConformanceTest.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
set DASHCRIMSON=
:end
