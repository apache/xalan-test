@echo off
rem
rem ==========================================================================
rem = Copyright 2004 The Apache Software Foundation.
rem =
rem = Licensed under the Apache License, Version 2.0 (the "License");
rem = you may not use this file except in compliance with the License.
rem = You may obtain a copy of the License at
rem =
rem =     http://www.apache.org/licenses/LICENSE-2.0
rem =
rem = Unless required by applicable law or agreed to in writing, software
rem = distributed under the License is distributed on an "AS IS" BASIS,
rem = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem = See the License for the specific language governing permissions and
rem = limitations under the License.
rem ==========================================================================
rem
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