<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

<!-- FileName: multiViewResults.xsl -->
<!-- Author: shane_curcuru@lotus.com -->
<!-- Purpose: Viewer for multiple logFile results put into simple HTML pages -->
<!-- Usage: ...Process -in ListOfFiles.xml -xsl MultiViewResults.xsl ... -->
<!-- Where:
    ?xml version="1.0"?
    resultsfilelist fileName="ListOfFiles.xml" desc="Testing 1-2-3"
    resultsfile fileRef="TestResultsOne.xml"
    resultsfile fileRef="TestResultsTwo.xml"
    ...
    /resultsfilelist
-->

<!-- ======================================================= -->
<!-- Include the main results viewer for individual result files -->
<!-- Note: depends on being in the same directory -->
<xsl:import href="ViewResults.xsl"/>

<!-- ================================== -->
<!-- Main template: output an HTML page -->
<!-- The resultfile element must be present.  It should include one or more 
     testfile elements, and may include other elements in some cases.  -->
<xsl:template match="/resultsfilelist">
  <HTML>
  <HEAD><TITLE><xsl:text>Multiple Test Result list file: </xsl:text><xsl:value-of select="./@fileName"/></TITLE></HEAD>
  <BODY>
    <H1><xsl:text>Multiple Test Results from: </xsl:text><xsl:value-of select="@desc"/></H1>
    <a name="top"><xsl:text>Includes individual resultfile(s):</xsl:text></a>
    <BR />
    <UL>
      <xsl:for-each select="resultsfile">
        <LI>
          <xsl:element name="a">
            <xsl:attribute name="href">#<xsl:value-of select="@fileRef"/></xsl:attribute>
            <xsl:value-of select="@fileRef"/>
          </xsl:element>
        </LI>
      </xsl:for-each>
    </UL>
    <xsl:apply-templates select="resultsfile/@fileRef"></xsl:apply-templates>

  </BODY>
  </HTML>
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

<!-- Just call the included stylesheet to output each individual file -->
<xsl:template match="resultsfile">
    <xsl:call-template name="mainResultsFile"></xsl:call-template>
</xsl:template>

</xsl:stylesheet>
