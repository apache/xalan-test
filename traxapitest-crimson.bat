@echo off
@goto start
@REM	Name:   traxapitest.bat
@REM	Author: Shane_Curcuru@lotus.com

:start
set END_PKG=trax
set JAVA_OPTS=-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.crimson.jaxp.DocumentBuilderFactoryImpl -D=org.apache.crimson.jaxp.SAXParserFactoryImpl
@echo Wrapper using 'trax.%1 -load APITest.properties' with crimson instead of Xerces
call runtest.bat -crimson %1 -load APITest.properties %2 %3 %4 %5 %6 %7 %8 %9
set END_PKG=
set JAVA_OPTS=
:end
