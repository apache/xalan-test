#!/bin/sh

#	Name:   runtest.sh
#	Author: Shane Curcuru

echo "runtest.sh beginning..."

if [ "$1" = "-h" ] ; then
    echo build.sh - executes Xalan Java-based test automation
    echo   Usage:   build [target] [-D options]
    echo   Example: build api -DtestClass=TransformerAPITest -Dqetest.loggingLevel=30
    exit 1
fi

# If PARSER_JAR is not set, default to xercesImpl.jar
if [ "$PARSER_JAR" = "" ] ; then
    PARSER_JAR=xercesImpl.jar
fi

if [ "$ANT_HOME" = "" ] ; then
  # try to find ANT
  if [ -d /opt/ant ] ; then 
    ANT_HOME=/opt/ant
  fi

  if [ -d ${HOME}/opt/ant ] ; then 
    ANT_HOME=${HOME}/opt/ant
  fi

  # Otherwise, just default the one over in java
  ANT_HOME=../java
fi

if [ "$JAVA_HOME" != "" ] ; then
  if [ "$JAVACMD" = "" ] ; then 
    JAVACMD=$JAVA_HOME/bin/java
  fi
else
  if [ "$JAVACMD" = "" ] ; then 
    JAVACMD=java
  fi
fi
 
# If JARDIR is set, prepend all .jars there to our classpath
if [ "$JARDIR" != "" ] ; then
    DIRLIBS=${JARDIR}/*.jar
    for i in ${DIRLIBS}
    do
        if [ "$i" != "${DIRLIBS}" ] ; then
            _ANT_CP="$i":$_ANT_CP
        fi
    done
fi

# add in the dependency .jar files (copied from ant)
_ANT_CP=$_ANT_CP:$ANT_HOME/bin/ant.jar
DIRLIBS=${ANT_HOME}/lib/*.jar
for i in ${DIRLIBS}
do
    # if the directory is empty, then it will return the input string
    # this is stupid, so case for it
    if [ "$i" != "${DIRLIBS}" ] ; then
        _ANT_CP=$_ANT_CP:"$i"
    fi
done

if [ "$CLASSPATH" != "" ] ; then
  _ANT_CP=$_ANT_CP:$CLASSPATH
fi

if [ "$JAVA_HOME" != "" ] ; then
  if test -f $JAVA_HOME/lib/tools.jar ; then
    _ANT_CP=$_ANT_CP:$JAVA_HOME/lib/tools.jar
  fi

  if test -f $JAVA_HOME/lib/classes.zip ; then
    _ANT_CP=$_ANT_CP:$JAVA_HOME/lib/classes.zip
  fi
else
  echo "Warning: JAVA_HOME environment variable is not set."
  echo "  If build fails because sun.* classes could not be found"
  echo "  you will need to set the JAVA_HOME environment variable"
  echo "  to the installation directory of java."
fi

# supply JIKESPATH to Ant as jikes.class.path
if [ "$JIKESPATH" != "" ] ; then
  if [ "$ANT_OPTS" != "" ] ; then
    ANT_OPTS="$ANT_OPTS -Djikes.class.path=$JIKESPATH"
  else
    ANT_OPTS=-Djikes.class.path=$JIKESPATH
  fi
fi

echo Running: $JAVACMD -classpath "$_ANT_CP" -Dant.home="${ANT_HOME}" $ANT_OPTS org.apache.tools.ant.Main "$@"
$JAVACMD -classpath "$_ANT_CP" -Dant.home="${ANT_HOME}" $ANT_OPTS org.apache.tools.ant.Main "$@"

echo "runtest.sh complete!"
