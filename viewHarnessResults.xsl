<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect">

<!-- FileName: viewHarnessResults.xsl -->
<!-- Author: shane_curcuru@lotus.com -->
<!-- Purpose: Viewer for multiple XSLTestHarness results put into simple HTML pages -->
<!-- Usage: ...Process -in HarnessResults.xml -xsl MultiViewResults.xsl ... -->
<!-- Where: you've run XSLTestHarness over a number of files -->

<!-- ======================================================= -->
<!-- Include the main results viewer for individual result files -->
<!-- Note: depends on being in the same directory -->
<xsl:import href="viewResults.xsl"/>

<!-- HTML output specifically; move after import due to Xalan-J 1.x restriction -->
<xsl:output method="html"/>

<!-- Explicitly declare the redirect extension -->
<lxslt:component prefix="redirect" elements="write open close" functions="">
  <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
</lxslt:component>  

<!-- ======================================================= -->
<!-- Output mini-summary filename, used in redirect calls in viewResults -->
<xsl:param name="summaryfile">HarnessSummary.xml</xsl:param>

<!-- ================================== -->
<!-- Main template: output an HTML page -->
<!-- The resultfile element must be present.  It should include one or more 
     testfile elements, and may include other elements in some cases.  -->
<xsl:template match="/resultsfile/testfile[@filename='XSLTestHarness']">
  <HTML>
  <HEAD><TITLE><xsl:text>Multiple Harness Test Result list file: </xsl:text><xsl:value-of select="./@logFile"/></TITLE></HEAD>
  <BODY>
    <H1><xsl:text>Multiple Harness Test Results from: </xsl:text><xsl:value-of select="@desc"/></H1>
    <a name="top"><xsl:text>Includes individual resultfile(s):</xsl:text></a>
    <UL>
      <xsl:for-each select="testcase/resultsfile">
        <LI>
          <xsl:element name="a">
            <xsl:attribute name="href">#<xsl:value-of select="@fileRef"/></xsl:attribute>
            <xsl:value-of select="@fileRef"/>
          </xsl:element>
          <xsl:text>  test result: </xsl:text>
          <xsl:variable name="testResult" select="document(@fileRef)/resultsfile/testfile/fileresult/@result"/>
          <xsl:choose>
            <xsl:when test="$testResult=$FAIL">
              <!-- can't reference $errfailcolor since it's not in scope here -->
              <FONT color="red"><xsl:value-of select="$testResult"/></FONT>
            </xsl:when>
            <xsl:when test="$testResult != $PASS">
              <I><xsl:value-of select="$testResult"/></I>
            </xsl:when>
            <xsl:otherwise>
              <FONT color="green"><xsl:value-of select="$testResult"/></FONT>
            </xsl:otherwise>
          </xsl:choose>
        </LI>
      </xsl:for-each>
    </UL>
    <P>
      <xsl:text>Total harness time: </xsl:text>
      <xsl:call-template name="total-time">
        <!-- Should use params to make this generic later on -->
      </xsl:call-template>
    </P>
    <H3><a name="harness-properties"><xsl:text>Harness-level System Properties:</xsl:text></a></H3>
    <xsl:apply-templates select="hashtable"></xsl:apply-templates>
    <H3><xsl:text>Individual resultfile(s) follow:</xsl:text></H3>
    <!-- Before processing any files, open up a summary file to 
         output a mini-summary of results to. -->
    <redirect:open select="$summaryfile" file="viewResults-redirected-output.xml"/>
      <redirect:write select="$summaryfile" file="viewResults-redirected-output.xml">
        <xsl:text disable-output-escaping="yes">&lt;?xml version="1.0"?>&#10;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;resultsfile logfile="</xsl:text>
        <xsl:value-of select="$summaryfile"/>
        <xsl:text disable-output-escaping="yes">">&#10;</xsl:text>
      </redirect:write>

        <xsl:apply-templates select="testcase/resultsfile/@fileRef"></xsl:apply-templates>

      <redirect:write select="$summaryfile" file="viewResults-redirected-output.xml">
        <xsl:text disable-output-escaping="yes">&#10;&lt;/resultsfile></xsl:text>
      </redirect:write>
    <redirect:close select="$summaryfile" file="viewResults-redirected-output.xml"/>

  </BODY>
  </HTML>
</xsl:template>

<xsl:template name="total-time">
  <xsl:variable name="totalsec" select="((statistic[starts-with(@desc,$TEST_STOP)]/longval) - (statistic[starts-with(@desc,$TEST_START)]/longval)) div 1000"/>
  <xsl:value-of select="floor($totalsec div 3600)"/> hr <xsl:value-of select="floor(($totalsec mod 3600) div 60)"/> min <xsl:value-of select="floor(($totalsec mod 3600) mod 60)"/> sec<xsl:text>.</xsl:text>
</xsl:template>

<!-- Select the document of each fileRef, also put in an anchor
     It's much easier to put the anchor in here, since we're assured 
     that the href from above and the anchor here will match. -->
<xsl:template match="resultsfile/@fileRef">
  <HR size="5" /> 
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="."/></xsl:attribute>
    <xsl:text> </xsl:text>
  </xsl:element>
  <FONT size="-1"><A HREF="#top"><xsl:text>Top of file</xsl:text></A></FONT>
  <xsl:apply-templates select="document(.)"></xsl:apply-templates>
</xsl:template>

<!-- Just call the included stylesheet to output each individual file 
     Note that these stylesheets grew (accreted) over time, and should 
     really be redesigned to make it simpler to plug in different 
     kinds of templates for various reporting types, like performance 
     charts and what-not.  Yet another place where the first time you 
     implement some xsl it's hard, and then you have the 'ah-ha!' moment 
     about the xsl way to do something.
-->
<xsl:template match="resultsfile">
    <!-- Should use params to make this generic later on -->
    <xsl:call-template name="mainResultsFile"></xsl:call-template>
</xsl:template>

</xsl:stylesheet>
