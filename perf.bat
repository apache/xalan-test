@echo off
@goto start
@REM	Name:   perf.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run StylesheetTestletDriver over tests\perf

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift

@echo Currently defaulting JAVA_OPTS to include -mx64m for performance measurements
set SJAVA_OPTS=%JAVA_OPTS%
set JAVA_OPTS=-mx64m %JAVA_OPTS%

set END_PKG=xsl
@echo Wrapper using '%DASHCRIMSON% xsl.StylesheetTestletDriver -load perf.properties %1 %2 %3 %4 %5 %6 %7 %8 %9'
call runtest.bat %DASHCRIMSON% StylesheetTestletDriver -load perf.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=

@REM Automatically attempt to style the results into HTML
call viewResults.bat %DASHCRIMSON% results-perf\results.xml results-perf\results.html -param summaryfile results-perf\results.txt

:end
set DASHCRIMSON=
set JAVA_OPTS=%SJAVA_OPTS%
