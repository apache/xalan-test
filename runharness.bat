@echo off
@goto start
@REM	Name:   harness.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
set END_PKG=xsl
@echo Harness Wrapper using 'xsl.XSLTestHarness Harness.properties'
@echo Running multiple tests= from Harness.properties now...
call runtest.bat XSLTestHarness Harness.properties %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
:end
