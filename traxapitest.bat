@echo This batch file is deprecated; for an equivalent try:
@echo build api -DtestClass=TransformerAPITest
@goto end

@echo off
@goto start
@REM	Name:   traxapitest.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper for runtest.bat to run various Xalan-J 2.x 
@REM    API tests for javax.xml.transform or 'TRAX'

:start
@REM Pass along -crimson if it's the first arg
if '%1' == '-crimson' set DASHCRIMSON=-crimson
if '%1' == '-crimson' shift
set END_PKG=trax
@echo Wrapper using '%DASHCRIMSON% trax.%1 -load APITest.properties'
call runtest.bat %DASHCRIMSON% %1 -load APITest.properties %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
set DASHCRIMSON=
:end
