@echo off
@goto start
@REM	Name:   debugapi.bat
@REM	Author: Shane_Curcuru@us.ibm.com
@REM Wrapper for running tests without Ant in a debugger
@REM Usage: debugapi.bat org.apache.qetest.package.TestName [other options]

:start
set APICP=..\java\build\xalan.jar;..\java\bin\xml-apis.jar;..\java\bin\xercesImpl.jar;java\build\testxsl.jar;%CLASSPATH%
@echo Executing: java -classpath %APICP% %1 -load debugapi.properties %2 %3 %4 %5 %6 %7 %8 %9
java -classpath %APICP% %1 -load debugapi.properties %2 %3 %4 %5 %6 %7 %8 %9
set APICP=
:end
