@echo off
@goto start
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
@REM	Name:   threads.bat
@REM	Author: Shane_Curcuru@lotus.com
@REM Simple wrapper to run ThreadedTestletDriver with 
@REM    threads.properties and threads.filelist
@REM See build.bat/.xml for how to pass -Dqetest.foo=bar options

:start
@echo Redirect to build.bat threads %1 %2 %3 %4 %5 %6 %7 %8 %9
call build.bat %DASHCRIMSON% threads %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
