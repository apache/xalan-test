@echo off
@goto start
@REM	Name:   runtest.bat
@REM	Author: Shane_Curcuru@lotus.com
:usage
@echo runtest.bat - runs Xalan-J test automation
@echo.
@echo Notes/prerequisites: 
@echo   JAVA_OPTS Will be passed to java.exe or jview.exe
@echo   EXTRA_CP Will be appended to the classpath
@echo   END_PKG Will be the subpackage name after org.apache.qetest
@echo Common args include (from the java file, ignore first two "ERROR" lines): 
@REM Call with illegal arg to force Java code to print usage()
java -classpath %CLASSPATH%;testxsl.jar;%JARDIR%\testxsl.jar org.apache.qetest.xsl.XSLProcessorTestBase -load
@echo.
goto done
@REM ------------------------------------------------------------------------

:start
@echo %0 beginning...
@REM must have an option to continue
if '%1' == '' goto usage
if '%1' == '-h' goto usage
if '%1' == '-H' goto usage
if '%1' == '-?' goto usage
if '%1' == '-jview' goto setjv
if '%1' == '-JVIEW' goto setjv

@REM Non-jview: Trickery to guess appropriate location of java.exe program
if "%JAVA_HOME%" == "" set JAVA_EXE=java
if not "%JAVA_HOME%" == "" set JAVA_EXE=%JAVA_HOME%\bin\java
set CMDCP=-classpath
goto dojardir

:setjv
@REM -jview: Run Microsoft's jview instead of java
@REM    Note: We assume user has setup environment correctly!
@REM    shift to get rid of -jview arg; just pass rest of args along
shift
set JAVA_EXE=jview
set CMDCP=/cp
goto dojardir

:dojardir
@REM If JARDIR blank, use the existing classpath first, so we pick up 
@REM    any classes the user may have specified before default ones
@REM Note that this could cause conflicts if the user only specified 
@REM    some of the needed jars in the classpath, and then we add separate
@REM    copies of them later on.
if "%JARDIR%" == "" echo runtest.bat must have JARDIR set!
if "%JARDIR%" == "" goto done

@REM If JARDIR set, put those references first then default classpath
if not "%JARDIR%" == "" set TEST_CP=%JARDIR%\testxsl.jar;%JARDIR%\xerces.jar;%JARDIR%\xalan.jar;%JARDIR%\bsf.jar;%JARDIR%\js.jar;%CLASSPATH%

@REM Wrappers use EXTRA_CP to add items to our classpath; if set, append
if not "%EXTRA_CP%" == "" set TEST_CP=%TEST_CP%;%EXTRA_CP%

@REM Wrappers use END_PKG to switch around the end of the packagename; if not set, default it
if '%END_PKG%' == '' set END_PKG=xsl

@REM If exactly one option passed, assume we run: %JAVA_EXE% TestName -load TestName.prop
@REM this is a convenience for most tests, if they have a same-named prop file with them
if "%2" == "" echo "%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.%END_PKG%.%1 -load %1.prop
if "%2" == "" "%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.%END_PKG%.%1 -load %1.prop
if "%2" == "" goto done

@REM Otherwise, assume it's a bare classname to run with extra args
echo "%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.%END_PKG%.%1  %2 %3 %4 %5 %6 %7 %8 %9
"%JAVA_EXE%" %JAVA_OPTS% %CMDCP% "%TEST_CP%" org.apache.qetest.%END_PKG%.%1  %2 %3 %4 %5 %6 %7 %8 %9

:done
@echo %0 complete!
set TEST_CP=
set JAVA_EXE=
set CMDCP=
if '%END_PKG%' == 'xsl' set END_PKG=
:end
