@echo off
@goto start
@REM	Name:   viewResults.bat
@REM	Author: Shane_Curcuru@lotus.com
:usage
@REM ------------------------------------------------------------------------
:usage
@echo viewResults.bat - transforms a result.xml into results.html
@echo   Usage: results results\MyTest.xml [MyResults.html [options]]
@echo.
@echo   NOTE:  See runtest.bat for how to set CLASSPATH or JARDIR
goto done
@REM ------------------------------------------------------------------------

:start
@REM must have name of a results.xml file
if '%1' == '' goto usage
if '%1' == '-h' goto usage
if '%1' == '-H' goto usage
if '%1' == '-?' goto usage
@echo %0 beginning...

@REM Trickery to guess appropriate location of java.exe program
if '%JAVA_HOME%' == '' set JAVA_EXE=java
if not '%JAVA_HOME%' == '' set JAVA_EXE=%JAVA_HOME%\bin\java

@REM Same logic as runtest.bat
@REM @todo improve so all relevant batch/shell files use same logic -sc
if '%JARDIR%' == '' echo viewResults.bat must have JARDIR set!
if '%JARDIR%' == '' goto done

@REM If JARDIR set, put those references first then default classpath
if not '%JARDIR%' == '' set TEST_CP=%JARDIR%\testxsl.jar;%JARDIR%\xerces.jar;%JARDIR%\xalan.jar;%JARDIR%\bsf.jar;%JARDIR%\js.jar;%CLASSPATH%

@REM Set our output filename
if '%2' == '' set ROUT=results.html
if not '%2' == '' set ROUT=%2
if '%VSXSL%' == '' set VSXSL=viewResults.xsl
                             
@REM @todo find OS-independent way to send 'all remaining args' instead of just up to %9
echo %JAVA_EXE% %JAVA_OPTS% -classpath %TEST_CP% org.apache.xalan.xslt.Process -in %1 -xsl %VSXSL% -out %ROUT% %3 %4 %5 %6 %7 %8 %9
%JAVA_EXE% %JAVA_OPTS% -classpath %TEST_CP% org.apache.xalan.xslt.Process -in %1 -xsl %VSXSL% -out %ROUT% %3 %4 %5 %6 %7 %8 %9

:done
@echo %0 complete!
set TEST_CP=
set JAVA_EXE=
set ROUT=
set VSXSL=
:end
