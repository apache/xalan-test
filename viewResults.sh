#!/bin/sh

#	Name:   viewResults.sh
#	Author: Shane Curcuru

echo viewResults.sh beginning...

if [ "$JAVA_HOME" = "" ] ; then
    echo You must set JAVA_HOME, sorry!
    exit 1
fi
if [ "$1" = "-h" ] ; then
    echo viewResults.sh - transforms a result.xml into results.html
    echo   Usage:    viewResults results/MyTest.xml [MyResults.html [options]]
    echo   Example:  viewResults results-api/APITest.xml APITestResults.html -loggingLevel 99
    echo Notes/prerequisites: 
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
TEST_CP="$JARDIR/$PARSER_JAR:$JARDIR/xalan.jar:$CLASSPATH"

RIN=$1
shift
ROUT=$1
shift
if [ "$ROUT" = "" ] ; then
    ROUT=results.html
fi

echo Transforming Results: $RIN into $ROUT
echo "$JAVA_HOME"/bin/java $JAVA_OPTS -classpath "$TEST_CP" org.apache.xalan.xslt.Process -in "$RIN" -xsl viewResults.xsl -out "$ROUT" $@

"$JAVA_HOME"/bin/java $JAVA_OPTS -classpath "$TEST_CP" org.apache.xalan.xslt.Process -in "$RIN" -xsl viewResults.xsl -out "$ROUT" $@

echo viewResults.sh complete!




