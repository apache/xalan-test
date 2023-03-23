@echo off

REM Set JAVA_HOME environment variable, for the local environment

if "%JAVA_HOME%"=="" goto noJavaHome

set XALAN_BUILD_DIR_PATH=..\..\..\xalan-java\build;..\..\..\build

set XERCES_ENDORSED_DIR_PATH=..\..\..\xalan-java\lib\endorsed;..\..\..\lib\endorsed

REM #Test 1 (Testing XalanJ integer truncation bug fix, with XalanJ XSLTC processor)
if exist "test1.class" (
  REM clear the result XalanJ translet file, if that exists
  del test1.class
)

%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XALAN_BUILD_DIR_PATH%;%XERCES_ENDORSED_DIR_PATH% org.apache.xalan.xslt.Process -XSLTC -IN int_trunc.xml -XSL int_trunc.xsl -SECURE -XX -XT 2>NUL

if exist "test1.class" (
    echo Test failed. Please solve this, before checking in! 
) else (
    echo Congratulations! The test passed!
)

REM #Test 2 (Testing bug fix of the jira issue XALANJ-2623, with XalanJ interpretive processor)
%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XALAN_BUILD_DIR_PATH%;%XERCES_ENDORSED_DIR_PATH% org.apache.xalan.xslt.Process -IN jira_xalanj_2623.xml -XSL jira_xalanj_2623.xsl > jira_xalanj_2623.out 

REM For this test to succeed, following XML Schema validation should pass
%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XERCES_ENDORSED_DIR_PATH% jaxp.SourceValidator -i jira_xalanj_2623.out -a jira_xalanj_2623.xsd -f

REM #Test 3 (Testing bug fix of the jira issue XALANJ-2623, with XalanJ XSLTC processor)
%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XALAN_BUILD_DIR_PATH%;%XERCES_ENDORSED_DIR_PATH% org.apache.xalan.xslt.Process -XSLTC -IN jira_xalanj_2623.xml -XSL jira_xalanj_2623.xsl > jira_xalanj_2623.out 

REM For this test to succeed, following XML Schema validation should pass
%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XERCES_ENDORSED_DIR_PATH% jaxp.SourceValidator -i jira_xalanj_2623.out -a jira_xalanj_2623.xsd -f

del jira_xalanj_2623.out

goto end

:noJavaHome
echo Warning: JAVA_HOME environment variable is not set

:end
set XALAN_BUILD_DIR_PATH=
set XERCES_ENDORSED_DIR_PATH= 