@echo off
@goto start
@REM	Name:   debugconf.bat
@REM	Author: Shane_Curcuru@us.ibm.com
@REM Wrapper for running tests without Ant in a debugger
@REM Usage: debugconf.bat [other options]

:start
set CONFCP=..\java\build\xalan.jar;..\java\bin\xml-apis.jar;..\java\bin\xercesImpl.jar;java\build\testxsl.jar;%CLASSPATH%
@echo Executing: java -classpath %CONFCP% org.apache.qetest.xsl.StylesheetTestletDriver -load debugconf.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
java -classpath %CONFCP% org.apache.qetest.xsl.StylesheetTestletDriver -load debugconf.properties %1 %2 %3 %4 %5 %6 %7 %8 %9
set CONFCP=
:end
