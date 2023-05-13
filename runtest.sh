#!/bin/sh
#
#=========================================================================
# Copyright 2000-2004 The Apache Software Foundation.
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
#	Name:   runtest.sh
#	Author: Shane Curcuru

echo runtest.sh beginning...

if [ "$JAVA_HOME" = "" ] ; then
    echo You must set JAVA_HOME, sorry!
    exit 1
fi
if [ "$1" = "-h" ] ; then
    echo runtest.sh - simple wrapper for running automated Java tests
    echo   Usage:    runtest subpackage.TestClassName [options]
    echo   Example:  runtest trax.TransformerAPITest -load APITest.properties -loggingLevel 99
    echo   Example:  runtest xsl.ConformanceTest -load ConformanceTest.properties
    echo Notes/prerequisites: 
    echo   - JAVA_OPTS Will be passed to java.exe;
    echo   - java.exe must be in JAVAHOME/bin or in the path;
    exit 1
fi

# Default to xerces.jar
PARSER_JAR=xerces.jar

# Check if we should use crimson.jar instead
if [ "$1" = "-crimson" ] ; then
    PARSER_JAR=crimson.jar
    shift
    JAVA_OPTS="-Dorg.xml.sax.driver=org.apache.crimson.parser.XMLReaderImpl $JAVA_OPTS"
fi

# Default to shipped name of xalan.jar
XALAN_JAR=xalan.jar


# Set classpath for our use based on JARDIR
# prepend JARDIR-referenced jars to classpath
TEST_CP="$JARDIR/testxsl.jar:$JARDIR/$PARSER_JAR:$JARDIR/$XALAN_JAR:$JARDIR/bsf.jar:$JARDIR/js.jar:$CLASSPATH"

echo Running test: $1
echo "$JAVA_HOME"/bin/java $JAVA_OPTS -classpath "$TEST_CP" org.apache.qetest.$@

"$JAVA_HOME"/bin/java $JAVA_OPTS -classpath "$TEST_CP" org.apache.qetest.$@

echo runtest.sh complete!




