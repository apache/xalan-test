<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect"
    version="1.0">

<!-- FileName: FailScanner.xsl -->
<!-- Author: shane_curcuru@us.ibm.com -->
<!-- Purpose: Stylesheet for org.apache.qetest.XMLFileLogger
     logFiles that only prints out failing (and error) results.
-->

<!-- Import the BaseScanner for most duties; note this must be first in file -->
<xsl:import href="BaseScanner.xsl"/>

<xsl:output method="html"
          doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

<lxslt:component prefix="redirect" elements="write open close" functions="">
    <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
</lxslt:component>  

<!-- Name of file for mini-fails redirected output -->
<xsl:param name="redirectFilename">FailScannerMini.html</xsl:param>

<xsl:template name="printScannerName">
  <xsl:element name="a">
    <xsl:attribute name="href"><xsl:text>src/xsl/FailScanner.xsl</xsl:text></xsl:attribute>
    <xsl:text>FailScanner</xsl:text>
  </xsl:element>
</xsl:template>
<!-- ================================== -->
<!-- Element templates: output basic data for each of the common 
     test elements, like messages, checks, etc.
-->
<!-- All non-fail testcases just get a status line, nothing else -->
<xsl:template match="testcase[caseresult/@result = $PASS] | testcase[caseresult/@result = $INCP]">
  <h3>
    <xsl:value-of select="caseresult/@result"/>
    <xsl:text> Testcase: # </xsl:text>
    <!-- This creates the anchor as well as printing the @desc -->
    <xsl:call-template name="create-testcase-anchor">
      <xsl:with-param name="testcase" select="."/>
    </xsl:call-template>
    <br/>
  </h3>
</xsl:template>

<!-- Process all remaining testcases with special fails mode -->
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
    <!-- Only select results; the checkresult templates have special
         processing to output additional messages when needed.
    -->
    <xsl:apply-templates select="checkresult" />
    <tr>
      <td><br/><hr/></td>
      <td><xsl:value-of select="caseresult/@result"/>:<xsl:text>Testcase #</xsl:text><xsl:value-of select="@desc"/></td>
    </tr>
  </table>
</xsl:template>

<!-- Results that are clearly fails have preceding messages printed out;
     since those help in debugging and may indicate cause of failure.
-->
<xsl:template match="checkresult[@result=$FAIL] | checkresult[@result=$ERRR]">
  <xsl:apply-templates select="preceding-sibling::*[2]" mode="table"/>
  <xsl:apply-templates select="preceding-sibling::*[1]" mode="table"/>
    
  <!-- Then print out this result itself -->
  <tr>
    <td bgcolor="#FF8080">
      <xsl:call-template name="create-checkresult-anchor">
        <xsl:with-param name="checkresult" select="."/>
      </xsl:call-template>
    </td>
    <td>
      <xsl:if test="@id">
        <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
      </xsl:if>
      <xsl:value-of select="@desc"/>
    </td>
  </tr>

  <!-- Then print out blank row to separate results -->
  <tr><td colspan="2" bgcolor="silver"><xsl:text> </xsl:text></td></tr>

  <redirect:write select="$redirectFilename" append="true">
    <b><xsl:value-of select="@result"/><xsl:text> </xsl:text></b>
    <xsl:if test="@id">
      <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
    </xsl:if>
    <xsl:value-of select="@desc"/><br/>
  </redirect:write>
</xsl:template>

<!-- Other Results that are not passes just get printed out as-is -->
<xsl:template match="checkresult[@result=$AMBG] | checkresult[@result=$INCP]">
  <xsl:apply-templates select="preceding-sibling::fileCheck[1]" mode="table"/>
  <tr>
    <td bgcolor="#FFFF00">
      <i>
        <xsl:call-template name="create-checkresult-anchor">
          <xsl:with-param name="checkresult" select="."/>
        </xsl:call-template>
      </i>
    </td>
    <td>
      <xsl:if test="@id">
        <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
      </xsl:if>
      <xsl:value-of select="@desc"/>
    </td>
  </tr>
  <!-- Then print out blank row to separate results -->
  <tr><td colspan="2" bgcolor="silver"><xsl:text> </xsl:text></td></tr>
</xsl:template>

<!-- Override BaseScanner handling of non-fail checkresults as no-op -->
<xsl:template match="checkresult" />

<!-- Override most messages when not in a testcase -->
<xsl:template match="message[@level > $WARNINGMSG]" />

</xsl:stylesheet>
