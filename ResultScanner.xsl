<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="1.0">
  <xsl:output method="html"
              doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

<!-- FileName: ResultScanner.xsl -->
<!-- Author: shane_curcuru@us.ibm.com -->
<!-- Purpose: Meta-scanner for groups of results produced 
     by org.apache.qetest.ResultScanner -->

<!-- Basic scanning stylesheet for individual testfile results -->
<xsl:include href="FailScanner.xsl"/>

<!-- ================================== -->
<!-- Constants from org.apache.qetest.ResultScanner -->
<xsl:variable name="goodResult">ok</xsl:variable>
<xsl:variable name="badResult">notok</xsl:variable>

<!-- ================================== -->
<!-- Main template: output an HTML page -->
<xsl:template match="resultfilelist">
  <html>
    <head>
      <title><xsl:text>ResultScanner: </xsl:text><xsl:value-of select="@logFile"/></title>
    </head>
    <body>
      <!-- A table of all fail results -->
      <h3>All bad results:</h3>
      <xsl:for-each select="testgroup/teststatus[@status = $badResult]">
          <xsl:variable name="linkname" select="concat(../@href, '/', @href)" />
          <xsl:element name="a">
            <xsl:attribute name="href">#<xsl:value-of select="$linkname"/></xsl:attribute>
            <xsl:value-of select="$linkname"/>
          </xsl:element>
          <xsl:call-template name="miniStatusTable" />
          <br/>
      </xsl:for-each>
      <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<!-- For each bad file, pre-read summary -->
<xsl:template name="miniStatusTable">
  <!-- Note this entire template is quite inefficient, since it 
        forces an extra loading of the status doc that will be 
        loaded again later on, but it works for now.
  -->
  <!-- Read in the teststatus file we found and get it's info -->
  <xsl:variable name="statusfilename" select="concat(../@href, '/', @href)"/>
  <xsl:variable name="status" select="document($statusfilename)/teststatus"/>
  <xsl:text> P: </xsl:text>
  <xsl:value-of select="$status/@Pass-cases"/><xsl:text>/</xsl:text>
  <xsl:value-of select="$status/@Pass-checks"/><xsl:text>, </xsl:text>
  <xsl:text>F: </xsl:text>
  <xsl:value-of select="$status/@Fail-cases"/><xsl:text>/</xsl:text>
  <xsl:value-of select="$status/@Fail-checks"/><xsl:text>, </xsl:text>
  <xsl:text>E: </xsl:text>
  <xsl:value-of select="$status/@Errr-cases"/><xsl:text>/</xsl:text>
  <xsl:value-of select="$status/@Errr-checks"/><xsl:text>, </xsl:text>
  <xsl:text>A: </xsl:text>
  <xsl:value-of select="$status/@Ambg-cases"/><xsl:text>/</xsl:text>
  <xsl:value-of select="$status/@Ambg-checks"/><xsl:text>, </xsl:text>
  <xsl:text>I: </xsl:text>
  <xsl:value-of select="$status/@Incp-cases"/>

</xsl:template>

<!-- For each directory full of results found, print out a summary -->
<xsl:template match="testgroup">
  <!-- Cache the value of the dir for later use -->
  <xsl:variable name="dir" select="@href"/>
  <h3><xsl:text>Testgroup: </xsl:text><xsl:value-of select="$dir"/></h3>
  <font size="-1">
    <xsl:apply-templates select="teststatus[@status = $goodResult]" />
  </font>
  <br/>
  <xsl:apply-templates select="teststatus[@status = $badResult]" >
      <xsl:with-param name="dir" select="$dir"/>
  </xsl:apply-templates>
</xsl:template>

<!-- Just list names of good result files (passing/ambg), nothing else -->
<xsl:template match="teststatus[@status = $goodResult][starts-with(@href, 'Ambg')]">
  <b><xsl:value-of select="@href"/></b><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="teststatus[@status = $goodResult]">
  <xsl:value-of select="@href"/><xsl:text> </xsl:text>
</xsl:template>

<!-- For bad result summary files, go look up the actual file and report it -->
<xsl:template match="teststatus[@status = $badResult]">
  <xsl:param name="dir" select="."/>
  <br/>
  <!-- Read in the teststatus file we found and get it's info -->
  <xsl:variable name="statusfilename" select="concat($dir, '/', @href)"/>
  <xsl:variable name="status" select="document($statusfilename)/teststatus"/>

  <!-- From that info, read in the actual results file associated therewith -->
  <xsl:variable name="testresultsfilename" select="concat($status/@baseref, '/', $status/@logFile)"/>
  <xsl:variable name="testresults" select="document($testresultsfilename)/resultsfile"/>

  <!-- Anchor element for each test -->
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="$statusfilename"/></xsl:attribute>
  </xsl:element>

  <!-- Link to the full xml source of each test -->
  <font size="-1"><xsl:value-of select="$testresults/testfile/@filename"/>
    <xsl:element name="a">
      <xsl:attribute name="href"><xsl:value-of select="$testresultsfilename"/></xsl:attribute>
      <xsl:text> source results xml</xsl:text>
    </xsl:element>
  </font>

  <!-- Now scan the actual underlying result file for stuff -->
  <xsl:apply-templates select="$testresults/testfile"/>
</xsl:template>

<!-- Override default text node processing, so statistics, arbitrary messages, and other stuff is skipped -->
<xsl:template match="text()"/>

</xsl:stylesheet>
