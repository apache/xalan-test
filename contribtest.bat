@echo off
@goto start
@REM	Name:   contribtest.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
set END_PKG=xsl
@echo Wrapper using 'xsl.ConformanceTest -load ContribTest.properties'
call runtest.bat ConformanceTest -load ContribTest.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
:end
