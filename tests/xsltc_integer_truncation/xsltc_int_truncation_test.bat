@echo off

REM "Testing XalanJ integer truncation bug fix, when using XSLTC"

REM Set JAVA_HOME environment variable, for the local environment 

REM Set JARDIR environment variable, for the local environment.
REM For the definition of, JARDIR environment variable, please see 
REM the document https://gitbox.apache.org/repos/asf?p=xalan-java.git;a=blob_plain;f=README;hb=refs/heads/xalan-j_2_7_1_maint.

if EXIST "test1.class" (
  REM clear the result XalanJ translet file, if that exists
  DEL test1.class
)

%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%JARDIR% org.apache.xalan.xslt.Process -XSLTC -IN test1.xml -XSL test1.xsl -SECURE -XX -XT 2>NUL

if EXIST "test1.class" (
    echo Test failed. Please solve this, before checking in! 
) else (
    echo CONGRATULATIONS! The test passed!
)