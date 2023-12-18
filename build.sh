#!/bin/sh
#
#=========================================================================
# Copyright 2001-2023 The Apache Software Foundation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#=========================================================================
#
#	Name:   build.sh
#	Author: Joe Kesselman
#		Fresh port from Mukul Gandhi's revised build.bat.
#		WARNING: This currently does not include the hooks needed
#		to make the script compatable with cygwin (unix/Linux shell
#		and commands ported to run under Windows). See
#		deprecated_build.sh to see how we handled the cygwin
#		syntax differences back in 2001. These days, Windows users
#		are more likely to use WSL, which simplifies matters.

#	See:	build.xml

#	Setup:
#          1) You must set JAVA_HOME, for example,
#	      $ export JAVA_HOME=/etc/alternatives/java_sdk

#          2) You can set ANT_HOME if you use your own Ant install, for example,
#	      $ export ANT_HOME=/usr/share/ant

echo
echo Xalan-J test automation build
echo -----------------------------

if [ "$1" = "-h" ]; then
    echo build.sh - executes Xalan Java-based test automation
    echo   Usage:   build [target] [-D options]
    echo   Example: build api -DtestClass=TransformerAPITest -Dqetest.loggingLevel=30
    echo
    echo You MUST export the JAVA_HOME environment variable to point to the JDK
    echo You CAN export ANT_HOME environment variable if you use your own Ant install

    exit 1
fi

if [ "$JAVA_HOME" = "" ]; then
    echo Warning: JAVA_HOME environment variable is not exported
    echo You may have meant to set it to /etc/alternatives/java_sdk
    exit 1
fi

JAVACMD=$JAVA_HOME/bin/java

CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar

# Default is to use a copy of ant bundled with xalan-java
if [ "$ANT_HOME" = "" ]; then
   _ANT_HOME=../xalan-java
else
   _ANT_HOME=$ANT_HOME
fi

# Check user's ANT_HOME to make sure it actually has what we need
if [ -f "$_ANT_HOME/tools/ant.jar" ]; then
    _ANT_JARS=$_ANT_HOME/tools/ant.jar
elif [ -f "$_ANT_HOME/../tools/ant.jar" ]; then
    _ANT_JARS=$_ANT_HOME/../tools/ant.jar
else
    _ANT_JARS=$_ANT_HOME/lib/ant.jar:$_ANT_HOME/lib/ant-launcher.jar
fi

CLASSPATH=$CLASSPATH:$_ANT_JARS

# NOTE: deprecated_build.sh had a bit more fallback searching for java and
# ant resources, plus more hooks for overriding paths and parameters. We
# found those occasionally useful during development, so we left them in the
# standard scripts. But they aren't strictly needed.

XALAN_BUILD_CLASSPATH=../xalan-java/build/*:../build/*
XERCES_ENDORSED_CLASSPATH=../xalan-java/lib:../lib:../xalan-java/lib/endorsed:../lib/endorsed
XERCES_IMPL_CLASSPATH=../xalan-java/lib/*:../lib/*:../xalan-java/lib/endorsed/*:../lib/endorsed/*

# Override JRE defaults to set our own, preferring the "real" Apache code
# to the shadowed version that ships with the JRE.
JAXP_USE_APACHE="-Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl"

# Endorsed should no longer be necessary, given JAXP/TrAX overrides above.
# Just make sure they're on the classpaths.
# USE_OLD_ENDORSED_DIRS=-Djava.endorsed.dirs=$XALAN_BUILD_CLASSPATH:$XERCES_ENDORSED_CLASSPATH
CLASSPATH=$XALAN_BUILD_CLASSPATH:$XERCES_IMPL_CLASSPATH:$CLASSPATH

# Reminder: Note $* versus $@ distinction
# Also note classpath must be quoted to prevent CLI expansion of *
echo Running: "$JAVACMD" -mx1024m $USE_OLD_ENDORSED_DIRS -classpath "$CLASSPATH" $JAXP_USE_APACHE org.apache.tools.ant.Main "$@"
"$JAVACMD" -mx1024m $USE_OLD_ENDORSED_DIRS -classpath "$CLASSPATH" $JAXP_USE_APACHE org.apache.tools.ant.Main "$@"

echo "build.sh complete!"
