<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

<!-- FileName: viewResults.xsl -->
<!-- Author: shane_curcuru@lotus.com -->
<!-- Purpose: Handy viewer for org.apache.qetest.XMLFileLogger
     logFile results put into simple HTML pages -->

<!-- Include constant definitions for results file elements, 
     attributes, and values, copied from relevant Java code -->
<xsl:include href="resultsConstants.xsl"/>

<!-- ======================================================= -->
<!-- INPUT PARAMETERS can be set from external command lines -->
<!-- When set to true, skips (does not) output any checkresult[@result='PASS'] -->
<xsl:param name="failsonly">false</xsl:param>

<!-- File listing known bugs, so that when we get a result
     checkresult[@result='$FAIL'] who has an @id that also 
     matches a known bug, we can print 'SPR' instead of 'Fail' -->
<xsl:param name="bugfile">../java/bugs.xml</xsl:param>

<!-- Skip any message[@level&lt;=loggingLevel] -->
<xsl:param name="loggingLevel">50</xsl:param>

<!-- Only print out a bare summary; nothing else -->
<xsl:param name="summary">false</xsl:param>

<!-- Cheap color background for errors, fails, and messages likely related thereto -->
<xsl:param name="errfailcolor">red</xsl:param>
<xsl:param name="knownfailcolor">pink</xsl:param>

<!-- ================================== -->
<!-- Main template-standalone: output an HTML page -->
<!-- The resultfile element must be present.  It should include one or more 
     testfile elements, and may include other elements in some cases.  -->
<xsl:template match="resultsfile">
  <HTML>
  <HEAD><TITLE><xsl:text>Test Results file: </xsl:text><xsl:value-of select="./@logFile"/></TITLE></HEAD>
  <BODY>
    <!-- TODO: This case is only valid if we have a single testfile!!! -->
    <xsl:call-template name="doResultsFile"/>
  </BODY>
  </HTML>
</xsl:template>

<!-- ================================== -->
<!-- Main template-multiple-files: output an HTML page -->
<!-- The resultfile element must be present.  It should include one or more 
     testfile elements, and may include other elements in some cases.  -->
<!-- Note that this is only called when MultiViewResults.xsl is including us. --> 
<xsl:template name="mainResultsFile"> <!-- equivalent match="resultsfile" -->
  <H2><xsl:text>Test Results file: </xsl:text><xsl:value-of select="./@logFile"/></H2>
    <xsl:call-template name="doResultsFile"/>
</xsl:template>

<xsl:template name="doResultsFile"> <!-- equivalent match="resultsfile" -->
    <!-- We are expecting testfile and hashtable elements.  However a test 
         may also output other elements that we should also report -->
    <xsl:apply-templates select="testfile | hashtable | message | arbitrary | checkresult"/>
</xsl:template>

<!-- Grab the testfile's name and overall results first, then output the rest -->
<xsl:template match="testfile">
  <!-- Cache the value of the desc attribute for later use -->
  <xsl:variable name="testfilename" select="@filename"></xsl:variable>
  <H3><B><xsl:text>Testfile: </xsl:text><xsl:value-of select="$testfilename"/>
         <xsl:text> begins: </xsl:text></B><xsl:value-of select="@desc"/></H3>
  <!-- First dump out an overall summary table -->
  <xsl:call-template name="restable">
    <xsl:with-param name="linkname" select="$testfilename"></xsl:with-param>
  </xsl:call-template>
  <!-- Simply skip the rest if we're only doing summary -->
    <xsl:if test="$summary='false'">
      <!-- Optimization: dump most info first; then any testfile-level 
           hashtables at the very end of the report. -->
      <!-- Create a link to the hashtables for this file -->
      <FONT size="-1">
          <xsl:element name="a">
            <xsl:attribute name="href"><xsl:value-of select="$hash-marker"/><xsl:value-of select="$testfilename"/></xsl:attribute>
            <xsl:text>Hashtables and System.properties</xsl:text>
          </xsl:element>
      </FONT>
      <xsl:apply-templates select="testcase | message | arbitrary | checkresult"/>
      <!-- @todo optimize so anchor only appears if there are hashtables -->
      <xsl:element name="a">
        <xsl:attribute name="name"><xsl:value-of select="$hash-marker"/><xsl:value-of select="$testfilename"/></xsl:attribute>
        <xsl:text> </xsl:text><!-- Only need a blank space to anchor to -->
      </xsl:element>
      <xsl:apply-templates select="hashtable"/>
      <H3><B><xsl:text>Testfile (</xsl:text><xsl:value-of select="fileresult/@result"/><xsl:text>) ends: </xsl:text></B>
        <xsl:value-of select="$testfilename"/></H3>
      <FONT size="-1">
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="$file-results-marker"/><xsl:value-of select="$testfilename"/></xsl:attribute>
          <xsl:text>Overall testfile results</xsl:text>
        </xsl:element>
      </FONT>

   </xsl:if>
   <P><xsl:text>Test time (milliseconds): </xsl:text><xsl:value-of select="(statistic[starts-with(@desc,$TEST_STOP)]/longval) - (statistic[starts-with(@desc,$TEST_START)]/longval)"/></P>
</xsl:template>


<!-- Process each testcase as a table in HTML -->
<xsl:template match="testcase">
  <TABLE frame="box" border="1" rules="groups" width="95%" cellspacing="2" cellpadding="5">
  <CAPTION><xsl:text>Testcase begins: </xsl:text><xsl:value-of select="@desc"/></CAPTION>
    <!-- fake row to establish widths -->
    <TR><TD width="20%"></TD><TD width="80%"></TD></TR>
    <!-- It is illegal for a testcase to contain another testcase, so don't bother 
         selecting them: however you must check for every other kind of item -->
    <!-- fileref comes from XSLDirectoryIterator when a test has a non-pass checkresult -->
    <xsl:apply-templates select="message | arbitrary | checkresult | hashtable | fileref"/>
    <TR><TD></TD><TD><xsl:text>Case time (milliseconds): </xsl:text>
    <xsl:value-of select="(statistic[starts-with(@desc,$CASE_STOP)]/longval) - (statistic[starts-with(@desc,$CASE_START)]/longval)"/></TD></TR>
    <!-- Print out the overall caseresult at the end -->
    <xsl:apply-templates select="caseresult"/>
  </TABLE>
</xsl:template>

<!-- Note: must match values in XMLFileReporter for attributes and values! -->
<!-- Different processing for different kinds of checkresults, 
     normally only used within testcase elements -->
<xsl:template match="checkresult[@result=$FAIL] | checkresult[@result=$ERRR]">
  <TR>
    <xsl:choose>
    <!-- This needs to be updated to read some bugs.xml file, and 
         then print this for checkresults where the id is found in 
         bugs.xml somewhere
         /Sprs/Spr/Name
          -->
      <xsl:when test="@id = document($bugfile)/Sprs/Spr/Name">
        <TD bgcolor="{$knownfailcolor}"><B><xsl:text>Bug #</xsl:text><xsl:value-of select="@id"/></B></TD>
      </xsl:when>
      <xsl:when test="@id = 'known-bug'">
        <TD bgcolor="{$knownfailcolor}"><B><xsl:text>Reported bug</xsl:text></B></TD>
      </xsl:when>
      <xsl:otherwise>
        <TD bgcolor="{$errfailcolor}"><B><xsl:value-of select="@result"/></B></TD>
      </xsl:otherwise>
    </xsl:choose>
    <TD>
      <xsl:if test="@id">
        <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
      </xsl:if>
      <xsl:value-of select="@desc"/>
    </TD>
  </TR>
</xsl:template>

<xsl:template match="checkresult[@result=$AMBG] | checkresult[@result=$INCP]">
  <TR>
    <TD><I><xsl:value-of select="@result"/></I></TD>
    <TD>
      <xsl:if test="@id">
        <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
      </xsl:if>
      <xsl:value-of select="@desc"/>
    </TD>
  </TR>
</xsl:template>

<!-- If users want a 'condensed' report, set failsonly=true, and then we skip all pass records -->
<xsl:template match="checkresult[@result=$PASS]">
  <xsl:if test="$failsonly='false'">
    <TR><TD><xsl:value-of select="@result"/></TD><TD><xsl:value-of select="@desc"/></TD></TR>
  </xsl:if>
</xsl:template>

<!-- 
  Cheap-o way to output local filesystem links to the actual test files.
  We should actually tie the fileref together with the checkresult that 
  goes with it via fileref/@idref = checkresult/@id somehow, but 
  this would be closely related to XSLDirectoryIterator.
-->
<xsl:template match="fileref">
  <TR>
    <TD><xsl:text>[</xsl:text><xsl:value-of select="@idref"/><xsl:text>] </xsl:text></TD>
    <TD>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@inputName"/></xsl:attribute>
        <xsl:text>xsl</xsl:text>
      </xsl:element><xsl:text>, </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@xmlName"/></xsl:attribute>
        <xsl:text>xml</xsl:text>
      </xsl:element><xsl:text>, </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@outputName"/></xsl:attribute>
        <xsl:text>output</xsl:text>
      </xsl:element><xsl:text>, </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@goldName"/></xsl:attribute>
        <xsl:text>gold</xsl:text>
      </xsl:element>
    </TD>
  </TR>
</xsl:template>

<!-- Differentiate results that are not within a testcase! 
     These get their own table element so they stand out.  Also, 
     use trickery: the more specific template should be called for 
     Errr or Fail results below. -->
<xsl:template match="checkresult[parent::resultsfile or parent::testfile]">
  <TABLE frame="box" border="1" width="80%" cellspacing="1"><TR><TD><B><xsl:value-of select="@result"/></B></TD><TD><xsl:value-of select="@desc"/></TD></TR></TABLE>
</xsl:template>
<xsl:template match="checkresult[@result=$FAIL or @result=$ERRR][parent::resultsfile or parent::testfile]">
  <TABLE frame="box" border="1" width="80%" cellspacing="1"><TR><TD bgcolor="red"><B><xsl:value-of select="@result"/></B></TD><TD><xsl:value-of select="@desc"/></TD></TR></TABLE>
</xsl:template>

<!-- Only print out messages that are below or equal to our logging level -->
<!-- Note we should 'prettify' these when they're not within a testcase -->
<xsl:template match="message[@level&lt;=$loggingLevel]">
  <xsl:choose>
  <!-- Special processing when it's outside of a testcase! -->
    <xsl:when test="parent::resultsfile or parent::testfile">
      <TABLE frame="box" border="1" width="80%" cellspacing="1">
      <xsl:call-template name="othermessage"/>
      </TABLE>
    </xsl:when>
  <!-- Otherwise, different processing when it's related to a fail or error -->
    <xsl:when test="following-sibling::checkresult[position()=1]/@result=$ERRR">
      <xsl:call-template name="errfailmessage"/>
    </xsl:when>
    <xsl:when test="following-sibling::checkresult[position()=1]/@result=$FAIL">
      <xsl:call-template name="errfailmessage"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="othermessage"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- For messages, change the font size based on the level (which equates to importance) -->
<xsl:template name="othermessage">
  <TR>
  <xsl:choose>
    <xsl:when test="@level&lt;=$FAILSONLY">
    <TD><FONT size="+1"><xsl:value-of select="@level"/></FONT></TD><TD><FONT size="+1"><xsl:value-of select="."/></FONT></TD>
    </xsl:when>
    <xsl:when test="@level&gt;=$INFOMSG">
    <TD><FONT size="-1"><xsl:value-of select="@level"/></FONT></TD><TD><FONT size="-1"><xsl:value-of select="."/></FONT></TD>
    </xsl:when>
    <xsl:otherwise>
    <TD><xsl:value-of select="@level"/></TD><TD><xsl:value-of select="."/></TD>
    </xsl:otherwise>
  </xsl:choose>
  </TR>
</xsl:template>

<!-- Messages related to errors or fails get a special background -->
<xsl:template name="errfailmessage">
  <TR>
  <xsl:choose>
    <xsl:when test="@level&lt;=$FAILSONLY">
    <TD bgcolor="{$errfailcolor}"><FONT size="+1"><xsl:value-of select="@level"/></FONT></TD><TD><FONT size="+1"><xsl:value-of select="."/></FONT></TD>
    </xsl:when>
    <xsl:when test="@level&gt;=$INFOMSG">
    <TD bgcolor="{$errfailcolor}"><FONT size="-1"><xsl:value-of select="@level"/></FONT></TD><TD><FONT size="-1"><xsl:value-of select="."/></FONT></TD>
    </xsl:when>
    <xsl:otherwise>
    <TD bgcolor="{$errfailcolor}"><xsl:value-of select="@level"/></TD><TD><xsl:value-of select="."/></TD>
    </xsl:otherwise>
  </xsl:choose>
  </TR>
</xsl:template>


<!-- Likewise print out any arbitrary messages that are below or equal to our logging level -->
<!-- TODO: handle case where arbitrary is not a child of a testcase (which has a TABLE element already) -->
<xsl:template match="arbitrary[@level&lt;=$loggingLevel]">
  <TR><TD><FONT size="-1"><xsl:value-of select="@level"/></FONT></TD><TD><I><xsl:value-of select="."/></I></TD></TR>
</xsl:template>

<xsl:template match="caseresult">
  <TR><TD><xsl:text>----></xsl:text></TD><TD><xsl:text>Testcase result: </xsl:text><FONT size="+1"><xsl:value-of select="@result"/></FONT></TD></TR>
</xsl:template>

<!-- Dump hashtables and etc. FIXME: make settable so these can go at the end of a report -->
<!-- FIXME: have it simply call a hashitem template directly, since we know there 
     shouldn't be anything else in a hashtable besides hashitems...  -->
<xsl:template match="hashtable">
  <BR/><FONT size="-1"><xsl:text>Hashtable logged: </xsl:text><xsl:value-of select="@desc"/></FONT>
  <TABLE border="1" width="80%" cellspacing="1" cellpadding="2">
    <xsl:apply-templates select="hashitem"/>
  </TABLE>
</xsl:template>
<xsl:template match="hashtable[parent::testcase]">
  <TR><TD><xsl:text>---begin---</xsl:text></TD><TD><xsl:text>[Hashtable logged] </xsl:text><xsl:value-of select="@desc"/></TD></TR>
  <xsl:apply-templates select="hashitem"/>
  <TR><TD><xsl:text>----end----</xsl:text></TD><TD><xsl:text>[Hashtable logged] </xsl:text><xsl:value-of select="@desc"/></TD></TR>
</xsl:template>

<xsl:template match="hashitem">
  <TR><TD><xsl:value-of select="@key"/></TD><TD><xsl:value-of select="."/></TD></TR>
</xsl:template>

<!-- Special processing for java.class.path! This, more specific template must be after the generic one -->
<xsl:template match="hashitem[@key='java.class.path']">
  <TR><TD bgcolor="yellow"><xsl:value-of select="@key"/></TD><TD><xsl:value-of select="."/></TD></TR>
  <TR><TD bgcolor="yellow"></TD><TD><xsl:call-template name="ClasspathItems"></xsl:call-template></TD></TR>
</xsl:template>

<!-- Output the special messages that the Harness outputs showing which exact
     JARS it found on the actual classpath at runtime.  Note this is not terribly 
     efficient, and it may not be applicable to single test output. -->
<xsl:template name="ClasspathItems">
  <P><xsl:text>ClasspathItems found (when applicable): </xsl:text><BR/>
  <xsl:for-each select="//testfile/message[starts-with(.,'ClasspathItem')]">
    <xsl:value-of select="."/><BR/>
  </xsl:for-each></P>
</xsl:template>

<!-- Print an overall summary of pass/fail numbers.  Note that these are 
     grabbed from the testfile's statistics, and not actually counted as we parse. 
     This template is presumably called from the testfile level. -->

<xsl:template name="restable">
  <xsl:param name="linkname" select="none"/>
  <TABLE FRAME="box" BORDER="1" CELLPADDING="2" WIDTH="80%">
  <TR>
    <TD>
      <xsl:element name="a">
        <xsl:attribute name="name"><xsl:value-of select="$file-results-marker"/><xsl:value-of select="$linkname"/></xsl:attribute>
        <B><xsl:text>Overall Result: </xsl:text></B>
      </xsl:element>
      <xsl:value-of select="fileresult/@result"/>
    </TD>
    <TD>
      <B><xsl:text>Test Cases</xsl:text></B>
    </TD>
    <TD>
      <B><xsl:text>Test Points</xsl:text></B><xsl:text> (from script)</xsl:text>
    </TD>
    <TD>
      <B><xsl:text>Test Points</xsl:text></B><xsl:text> (from count)</xsl:text>
    </TD>
  </TR>

  <TR>
    <TD><xsl:text>Pass</xsl:text></TD>
    <TD><xsl:value-of select="./statistic[@desc='passCount[CASES]']/longval"/></TD>
    <TD><xsl:value-of select="statistic[@desc='passCount[CHECKS]']/longval"/></TD>
    <!-- Note this is horribly inefficent, but it gets the job done.
         I'd welcome any optimizations for this stylesheet!
    -->
    <TD><xsl:value-of select="count(//checkresult[@result=$PASS])"/></TD>
  </TR>
  <TR>
    <TD><B><xsl:text>Fail</xsl:text></B></TD>
    <TD><xsl:value-of select="./statistic[@desc='failCount[CASES]']/longval"/></TD>
    <TD><xsl:value-of select="statistic[@desc='failCount[CHECKS]']/longval"/></TD>
    <TD><xsl:value-of select="count(//checkresult[@result=$FAIL])"/></TD>
  </TR>
  <TR>
    <TD><I><xsl:text>Error</xsl:text></I></TD>
    <TD><xsl:value-of select="./statistic[@desc='errrCount[CASES]']/longval"/></TD>
    <TD><xsl:value-of select="statistic[@desc='errrCount[CHECKS]']/longval"/></TD>
    <TD><xsl:value-of select="count(//checkresult[@result=$ERRR])"/></TD>
  </TR>
  <TR>
    <TD><I><xsl:text>Ambiguous</xsl:text></I></TD>
    <TD><xsl:value-of select="./statistic[@desc='ambgCount[CASES]']/longval"/></TD>
    <TD><xsl:value-of select="statistic[@desc='ambgCount[CHECKS]']/longval"/></TD>
    <TD><xsl:value-of select="count(//checkresult[@result=$AMBG])"/></TD>
  </TR>
  <TR>
    <TD><I><xsl:text>Incomplete</xsl:text></I></TD>
    <TD><xsl:value-of select="./statistic[@desc='incpCount[CASES]']/longval"/></TD>
    <TD><xsl:value-of select="statistic[@desc='incpCount[CHECKS]']/longval"/></TD>
    <TD><xsl:value-of select="count(//checkresult[@result=$INCP])"/></TD>
  </TR>
  </TABLE><BR/>
</xsl:template>

<!-- Override default text node processing, so statistics, arbitrary messages, and other stuff is skipped -->
<xsl:template match="text()"></xsl:template>

</xsl:stylesheet>
