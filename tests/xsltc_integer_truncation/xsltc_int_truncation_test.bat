@echo off

REM "Testing XalanJ integer truncation bug fix, when using XSLTC"

REM Set JAVA_HOME environment variable, for the local environment

set XALAN_BUILD_DIR_PATH=..\..\xalan-java\build;..\..\build

set XERCES_ENDORSED_DIR_PATH=..\..\xalan-java\lib\endorsed;..\..\lib\endorsed

if exist "test1.class" (
  REM clear the result XalanJ translet file, if that exists
  del test1.class
)

REM in the future, we'll try to run this test as an API test
%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XALAN_BUILD_DIR_PATH%;%XERCES_ENDORSED_DIR_PATH% org.apache.xalan.xslt.Process -XSLTC -IN test1.xml -XSL test1.xsl -SECURE -XX -XT 2>NUL

if exist "test1.class" (
    echo Test failed. Please solve this, before checking in! 
) else (
    echo CONGRATULATIONS! The test passed!
)