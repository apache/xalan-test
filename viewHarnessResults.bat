@echo off
@goto start
@REM	Name:   viewHarnessResults.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
set VSXSL=viewHarnessResults.xsl
@REM Default values if not set, since harness always uses same logFile
if '%1' == '' goto calldefaults
call viewResults.bat %1 %2 %3 %4 %5 %6 %7 %8 %9
goto done

:calldefaults
call viewResults.bat results-api\Harness.xml results-api\Harness.html

:done
set VSXSL=
:end
