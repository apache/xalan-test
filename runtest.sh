#!/bin/sh

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
    JAVA_OPTS="-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.crimson.jaxp.DocumentBuilderFactoryImpl -D=org.apache.crimson.jaxp.SAXParserFactoryImpl $JAVA_OPTS"
fi

# Set classpath for our use based on JARDIR
# prepend JARDIR-referenced jars to classpath
TEST_CP="$JARDIR/testxsl.jar:$JARDIR/$PARSER_JAR:$JARDIR/xalan.jar:$JARDIR/bsf.jar:$JARDIR/js.jar:$CLASSPATH"

echo Running test: $1
echo "$JAVA_HOME"/bin/java $JAVA_OPTS -classpath "$TEST_CP" org.apache.qetest.$@

"$JAVA_HOME"/bin/java $JAVA_OPTS -classpath "$TEST_CP" org.apache.qetest.$@

echo runtest.sh complete!




