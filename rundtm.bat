@echo off
cls
REM This bat file is for testing the DTM.
rem ------------ Initialize variables -------------
set JAVAPROG=java
set savedCLASSPATH=%CLASSPATH%
set pathdist=..\java\bin\
set XCLASSPATH=%pathdist%xercesImpl.jar;%pathdist%xml-apis.jar;%pathdist%bsf.jar;%CLASSPATH%
set pathxalan=..\java\build\
set XCLASSPATH=%pathxalan%xalan.jar;%pathxalan%xsltc.jar;%XCLASSPATH%
set pathtest=.\java\build\
set XCLASSPATH=%pathtest%testxsl.jar;%XCLASSPATH%;.\
type blank.out
echo Classpath is:
echo %XCLASSPATH%
type blank.out
rem ------------ Initialization done ---------------
rem %JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.dtm.TestDTM %1
rem %JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.dtm.TestDTMIterator %1
rem %JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.dtm.TestDTMTraverser %1
rem %JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.dtm.TimeDTMTraverserFlat %1
rem %JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.dtm.TimeDTMTraverserDeep %1
rem %JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.dtm.TimeDTMIteratorFlat %1
rem %JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.dtm.TimeDTMIteratorDeep %1
%JAVAPROG% -Djava.compiler=NONE -mx256m -classpath %XCLASSPATH% org.apache.qetest.xsltc.TimeXSLTCDom %1 %2
set CLASSPATH=%savedCLASSPATH%
rem set XCLASSPATH=
set pathdist=
set pathxalan=
set pathtest=