@echo off
@goto start
@REM	Name:   conf.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run StylesheetTestletDriver over tests\conf

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=xsl
@echo Wrapper using '%DASHCRIMSON% xsl.StylesheetTestletDriver -load conf.properties %1 %2 %3 %4 %5 %6 %7 %8 %9'
call runtest.bat %DASHCRIMSON% StylesheetTestletDriver -load conf.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=

@REM Automatically attempt to style the results into HTML
@REM Note that the summaryfile param appears to be resolved relative 
@REM    to the output file, not the current dir (i.e. here, 
@REM    it will go into the same dir as the results)
call viewResults.bat %DASHCRIMSON% results-conf\results.xml results-conf\results.html -param summaryfile results.txt

:end
set DASHCRIMSON=
