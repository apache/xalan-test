#!/bin/sh

#	Name:   build.sh
#	Author: Shane Curcuru
#	Copyright (c)2000 Lotus Development Corp.

echo build.sh beginning...

if [ "$JAVA_HOME" = "" ] ; then
    echo You must set JAVA_HOME, sorry!
    exit 1
fi
if [ "$1" = "-h" ] ; then
    echo build.sh - compiles Xalan andor LotusXSL test automation
    echo   Usage:  build [targets]
    echo                 [all, package.xalan, package.lotusxsl, clean, javadoc]
    echo.
    echo Notes/prerequisites: 
    echo   - Must be in the xsl-test directory;
    echo   - java.exe must be in JAVAHOME/bin or in the path;
    echo   \(either\) set JARDIR=dir where all product/test jars needed are
    echo   \(either\) be in the ./xsl-test dir with all jars in 'default' tree
    echo   \(or\) have your CLASSPATH already set to include *all* needed jars
    echo   Pre-built 'default' tree assumed to be:
    echo   [rootDir]/    
    echo           xsl-test/test*.bat, testxsl.jar
    echo           xsl-test/conf/Extend, etc.
    echo           xml-xalan/xalan.jar, bsf.jar, bsfengines.jar, xerces.jar
    echo           js/js.jar       - appropriate JavaScript jar
    echo   JAVA_OPTS Will be passed to java.exe
    exit 1
fi

# Default locations for Ant; note home dir contains the bin dir
TANT_HOME=.
ANT=$TANT_HOME/bin/ant.jar

# Add whichever JDK's system classes to CLASSPATH we find
if [ -f $JAVA_HOME/lib/tools.jar ] ; then
    CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar
fi
if [ -f $JAVA_HOME/lib/classes.zip ] ; then
    CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/classes.zip
fi

# Set classpath for our use based on JARDIR
if [ "$JARDIR" = "" ] ; then
    # append default jar locations to classpath
    TEST_CP="$CLASSPATH:./build:./prod/extend:$ANT:$TOOLS_JAR:../xml-xalan/xalan.jar:../xml-xalan/xerces.jar:../xml-xalan/bsf.jar:../xml-xalan/bsfengines.jar"
else
    # prepend JARDIR-referenced jars to classpath
    TEST_CP="./build:./prod/extend:$ANT:$TOOLS_JAR:$JARDIR/xalan.jar:$JARDIR/xerces.jar:$JARDIR/bsf.jar:$JARDIR/bsfengines.jar:$CLASSPATH"
fi

echo Starting Ant with targets: $@
echo         ...with classpath: $TEST_CP

"$JAVA_HOME"/bin/java $JAVA_OPTS -Dant.home="$TANT_HOME" -classpath "$TEST_CP" org.apache.tools.ant.Main $@

echo build.sh complete!




