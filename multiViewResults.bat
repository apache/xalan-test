@echo off
@goto start
@REM	Name:   multiViewResults.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
set VSXSL=multiViewResults.xsl
call viewResults.bat %1 %2 %3 %4 %5 %6 %7 %8 %9
set VSXSL=
:end
