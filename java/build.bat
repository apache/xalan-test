@echo off
@goto start
@REM	Name:   build.bat
@REM	Author: Shane Curcuru
:usage
@echo build.bat - compiles Xalan Java-based test automation
@echo   Usage:  build [targets]
@echo                 [package.xalan1, package.trax, clean, javadocs]
@echo.
@echo Notes/prerequisites: 
@echo   - Must be in the xml-xalan\test\java directory;
@echo   - java.exe must be in %JAVAHOME%\bin or in the path;
@echo   - must set classpath to include JDK system classes; and
@echo   (either) set JARDIR=dir where all product/test jars needed are
@echo   (either) be in the xml-xalan\test\java dir with all jars in 'default' tree
@echo   (or) have your CLASSPATH already set to include *all* needed jars
@echo   Pre-built 'default' tree assumed to be:
@echo   [rootDir]\    
@echo   JAVA_OPTS environment variable Will be passed to java.exe
@echo.
goto done
@REM ------------------------------------------------------------------------

:start
@REM Check for help requests
if '%1' == '' goto usage
if '%1' == '-h' goto usage
if '%1' == '-H' goto usage
if '%1' == '-?' goto usage
@echo %0 beginning...

@REM Trickery to guess appropriate location of java.exe program
if "%JAVA_HOME%" == "" set JAVA_EXE=java
if not "%JAVA_HOME%" == "" set JAVA_EXE=%JAVA_HOME%\bin\java

set _ANT_HOME=.

@REM If PARSER_JAR blank, default to xerces
set _PARSER_JAR=%PARSER_JAR%
if "%_PARSER_JAR%" == "" set _PARSER_JAR=..\..\java\bin\xerces.jar
@REM Set other jars by environment variables as well
if "%_XALAN_JAR%" == "" set _XALAN_JAR=..\..\java\build\xalan.jar
if "%_BSF_JAR%" == "" set _BSF_JAR=..\..\java\bin\bsf.jar

@REM If JARDIR blank, use the existing classpath first, so we pick up 
@REM    any classes the user may have specified before default ones
@REM Note that this could cause conflicts if the user only specified 
@REM    some of the needed jars in the classpath, and then we add separate
@REM    copies of them later on.
@REM HACK! Includes only Xalan-J 2.x build
if "%JARDIR%" == "" set TEST_CP=.\build;%classpath%;%_ANT_HOME%\bin\ant.jar;%_PARSER_JAR%;%_XALAN_JAR%;%_BSF_JAR%

@REM If JARDIR set, put those references first then default classpath
@REM note that we still assume you're in xsl-test dir because of .\conf\Extend reference
if not "%JARDIR%" == "" set TEST_CP=.\build;%_ANT_HOME%\bin\ant.jar;%JARDIR%\xerces.jar;%JARDIR%\xalan.jar;%JARDIR%\bsf.jar;%classpath%

:doant
@REM Simply execute Ant to build it all
echo "%JAVA_EXE%" %JAVA_OPTS% -Dant.home=%_ANT_HOME% -classpath "%TEST_CP%" org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
"%JAVA_EXE%" %JAVA_OPTS% -Dant.home=%_ANT_HOME% -classpath "%TEST_CP%" org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
goto done

:done
@echo %0 complete!
set TEST_CP=
set JAVA_EXE=
set _ANT_HOME=
set _PARSER_JAR=
set _XALAN_JAR=
set _BSF_JAR=
:end
