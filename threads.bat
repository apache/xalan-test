@echo off
@goto start
@REM	Name:   threads.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run ThreadedTestletDriver with 
@REM    threads.properties and threads.filelist

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=xsl
@echo Wrapper using '%DASHCRIMSON% xsl.ThreadedTestletDriver -load threads.properties %1 %2 %3 %4 %5 %6 %7 %8 %9'
call runtest.bat %DASHCRIMSON% ThreadedTestletDriver -load threads.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=

@REM (Optional) Automatically attempt to style the results into HTML
call viewResults.bat %DASHCRIMSON% results-threads\results.xml results-threads\results.html -param summaryfile results.txt

:end
set DASHCRIMSON=
