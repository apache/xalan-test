#!/bin/sh
#
#=========================================================================
# Copyright 2004 The Apache Software Foundation.
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
#	Author: Shane Curcuru

echo "build.sh beginning..."

if [ "$1" = "-h" ] ; then
    echo build.sh - executes Xalan Java-based test automation
    echo   Usage:   build [target] [-D options]
    echo   Example: build api -DtestClass=TransformerAPITest -Dqetest.loggingLevel=30
    exit 1
fi
CLS_PATH_SEP=:
# if we're on windows, override that:
uname | grep WIN && CLS_PATH_SEP=\;

# If PARSER_JAR is not set, default to xercesImpl.jar
if [ "$PARSER_JAR" = "" ] ; then
    PARSER_JAR=../java/bin/xercesImpl.jar
fi

if [ "$XML_APIS_JAR" = "" ]; then
    XML_APIS_JAR=../java/bin/xml-apis.jar
fi

if [ "$ANT_HOME" = "" ] ; then
  # try to find ANT
  if [ -d /opt/ant ] ; then 
    ANT_HOME=/opt/ant
  elif [ -d ${HOME}/opt/ant ] ; then 
    ANT_HOME=${HOME}/opt/ant
  else
     # Otherwise, just default the one over in java
     ANT_HOME=../java
  fi
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
 
# add in the dependency .jar files (copied from ant)
DIRLIBS=${ANT_HOME}/lib/*.jar
_ANT_CP=$ANT_HOME/bin/ant.jar
for i in ${DIRLIBS}
do
    # if the directory is empty, then it will return the input string
    # this is stupid, so check for it
    if [ "$i" != "${DIRLIBS}" ] ; then
        _ANT_CP=$_ANT_CP${CLS_PATH_SEP}"$i"
    fi
done

# If JARDIR is set, prepend all .jars there to our classpath
if [ "$JARDIR" != "" ] ; then
    CLASSPATH=${_ANT_CP}${CLS_PATH_SEP}${CLASSPATH}

    DIRLIBS=${JARDIR}/*.jar
    for i in ${DIRLIBS}
    do
        if [ "$i" != "${DIRLIBS}" ] ; then
            CLASSPATH="$i"${CLS_PATH_SEP}${CLASSPATH}
        fi
    done
else
    CLASSPATH=${CLASSPATH}${CLS_PATH_SEP}${_ANT_CP}${CLS_PATH_SEP}${PARSER_JAR}${CLS_PATH_SEP}${XML_APIS_JAR}
fi

if [ "$JAVA_HOME" != "" ] ; then
  if test -f $JAVA_HOME/lib/tools.jar ; then
    CLASSPATH=${CLASSPATH}${CLS_PATH_SEP}${JAVA_HOME}/lib/tools.jar
  fi

  if test -f $JAVA_HOME/lib/classes.zip ; then
    CLASSPATH="${CLASSPATH}${CLS_PATH_SEP}${JAVA_HOME}/lib/classes.zip"
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

# also pass along the selected parser to Ant
ANT_OPTS="${ANT_OPTS} -Dparserjar=${PARSER_JAR}"

echo Running:  $JAVACMD ${JAVA_OPTS} -classpath "${CLASSPATH}" -Dant.home="${ANT_HOME}" $ANT_OPTS org.apache.tools.ant.Main "$@"
$JAVACMD ${JAVA_OPTS} -classpath "${CLASSPATH}" -Dant.home="${ANT_HOME}" $ANT_OPTS org.apache.tools.ant.Main "$@"

echo "build.sh complete!"
