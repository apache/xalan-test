<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect"
    version="1.0">

<!-- FileName: PerfScanner.xsl -->
<!-- Author: shane_curcuru@us.ibm.com -->
<!-- Purpose: Stylesheet for org.apache.qetest.XMLFileLogger
     logFiles that only prints out simple performance stats.
     Note that pdick is working on much more interesting 
     stylesheets that compare the perf results from one 
     test run to the previous one in a chart.
-->

<!-- Import the BaseScanner for most duties; note this must be first in file -->
<xsl:import href="BaseScanner.xsl"/>

<xsl:output method="html"
          doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

<lxslt:component prefix="redirect" elements="write open close" functions="">
    <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
</lxslt:component>  

<!-- Name of file for mini-fails redirected output -->
<xsl:param name="redirectFilename">PerfScannerMini.html</xsl:param>

<xsl:template name="printScannerName">
  <xsl:element name="a">
    <xsl:attribute name="href"><xsl:text>PerfScanner.xsl</xsl:text></xsl:attribute>
    <xsl:text>PerfScanner</xsl:text>
  </xsl:element>
</xsl:template>
<!-- ================================== -->
<!-- Element templates: output basic data for each of the common 
     test elements, like messages, checks, etc.
-->
<xsl:template match="testcase">
  <table frame="box" border="1" rules="groups" 
         cellspacing="2" cellpadding="2"
         bgcolor="#FFFFEE">
    <caption>
      <b><xsl:text>Testcase # </xsl:text></b>
      <!-- This creates the anchor as well as printing the @desc -->
      <xsl:call-template name="create-testcase-anchor">
        <xsl:with-param name="testcase" select="."/>
      </xsl:call-template>
    </caption>
    <!-- establish row widths here -->
    <tr>
      <td width="20"></td>
      <td></td>
    </tr>
    <!-- Only select results and perf items -->
    <xsl:apply-templates select="checkresult | perf" mode="table"/>
    <tr>
      <td><br/><hr/></td>
      <td><xsl:value-of select="caseresult/@result"/>:<xsl:text>Testcase #</xsl:text><xsl:value-of select="@desc"/></td>
    </tr>
  </table>
</xsl:template>

</xsl:stylesheet>
