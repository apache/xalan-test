<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect"
    version="1.0">
<xsl:output method="html"
          doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

<lxslt:component prefix="redirect" elements="write open close" functions="">
    <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
</lxslt:component>  

<!-- FileName: FailScanner.xsl -->
<!-- Author: shane_curcuru@us.ibm.com -->
<!-- Purpose: Simple viewer for org.apache.qetest.XMLFileLogger
     logFile failing-only results put into simple HTML pages updates coming -->

<!-- Include constant definitions for results file elements, 
     attributes, and values, copied from relevant Java code -->
<xsl:include href="resultsConstants.xsl"/>

<!-- Name of file for mini-fails redirected output -->
<xsl:param name="redirectFilename">FailScannerMini.html</xsl:param>

<!-- ================================== -->
<!-- Main template-standalone: output an HTML page -->
<xsl:template match="resultsfile">
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title><xsl:text>Test Results file: </xsl:text><xsl:value-of select="@logFile"/></title>
        </head>
        <body>
            <xsl:apply-templates/>
        </body>
    </html>
</xsl:template>

<!-- Output each whole testfile as a table; a small summary then individual results -->
<!-- This is selected in ResultScanner.xsl directly -->
<xsl:template match="testfile">
  <table frame="box" border="1" rules="groups" 
         cellspacing="2" cellpadding="2"
         bgcolor="#FFFFEE">
    <caption>
      <b><xsl:value-of select="@filename"/><xsl:text>: </xsl:text></b><xsl:value-of select="@desc"/>
    </caption>
    <redirect:write select="$redirectFilename" append="true">
      <hr/>
      <h3><xsl:value-of select="@filename"/>
      <xsl:text>: </xsl:text><xsl:value-of select="@desc"/>
      <xsl:text> in </xsl:text><xsl:value-of select="../@logFile"/>
      </h3>
    </redirect:write>
    <!-- establish row widths here in our mini-summary -->
    <tr>
      <td width="7%"><xsl:text>Overall</xsl:text></td>
      <td>
        <xsl:text>Pass: </xsl:text>
        <xsl:value-of select="statistic[@desc='passCount[CASES]']/longval"/><xsl:text>/</xsl:text>
        <xsl:value-of select="statistic[@desc='passCount[CHECKS]']/longval"/><xsl:text>, </xsl:text>
        <xsl:text>Fail: </xsl:text>
        <xsl:value-of select="statistic[@desc='failCount[CASES]']/longval"/><xsl:text>/</xsl:text>
        <xsl:value-of select="statistic[@desc='failCount[CHECKS]']/longval"/><xsl:text>, </xsl:text>
        <xsl:text>Errr: </xsl:text>
        <xsl:value-of select="statistic[@desc='errrCount[CASES]']/longval"/><xsl:text>/</xsl:text>
        <xsl:value-of select="statistic[@desc='errrCount[CHECKS]']/longval"/><xsl:text>, </xsl:text>
        <xsl:text>Ambg: </xsl:text>
        <xsl:value-of select="statistic[@desc='ambgCount[CASES]']/longval"/><xsl:text>/</xsl:text>
        <xsl:value-of select="statistic[@desc='ambgCount[CHECKS]']/longval"/><xsl:text>, </xsl:text>
        <xsl:text>Incp: </xsl:text>
        <xsl:value-of select="statistic[@desc='incpCount[CASES]']/longval"/>
      </td>
    </tr>
    
    <!-- Search on all subelements; don't bother restricting -->
    <xsl:apply-templates />
  </table>
</xsl:template>

<!-- Output only testcases that have clear fails with the specific fails -->
<xsl:template match="testcase[caseresult/@result = $FAIL] | testcase[caseresult/@result = $ERRR]">
    <tr>
      <td><br/><hr/></td>
      <td><xsl:text>Testcase #</xsl:text><xsl:value-of select="@desc"/><xsl:text> is </xsl:text><xsl:value-of select="caseresult/@result"/></td>
    </tr>
    <!-- Only select results; we only have matches later for failed results -->
    <xsl:apply-templates select="checkresult" />
</xsl:template>

<!-- Testcases that are incomplete just get a note, no details -->
<xsl:template match="testcase[caseresult/@result = $INCP]">
    <tr>
      <td><hr/></td>
      <td><xsl:text>Testcase #</xsl:text><xsl:value-of select="@desc"/><xsl:text> was </xsl:text><xsl:value-of select="caseresult/@result"/></td>
    </tr>
</xsl:template>

<!-- Results that are clearly fails have preceding messages printed out -->
<xsl:template match="checkresult[@result=$FAIL] | checkresult[@result=$ERRR]">
  <!-- First, find the several previous messages to this result -->
  <xsl:apply-templates select="preceding-sibling::*[2]" mode="before-result" />
  <xsl:apply-templates select="preceding-sibling::*[1]" mode="before-result" />
    
  <!-- Then print out this result itself -->
  <tr>
    <td bgcolor="#FF8080"><b><xsl:value-of select="@result"/></b></td>
    <td>
      <xsl:if test="@id">
        <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
      </xsl:if>
      <xsl:value-of select="@desc"/>
    </td>
  </tr>
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
  <xsl:apply-templates select="preceding-sibling::fileCheck[1]" mode="before-result" />
  <tr>
    <td bgcolor="#FFFF00"><i><xsl:value-of select="@result"/></i></td>
    <td>
      <xsl:if test="@id">
        <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
      </xsl:if>
      <xsl:value-of select="@desc"/>
    </td>
  </tr>
</xsl:template>

<!-- Only selected from previous checkresult apply-templates -->
<xsl:template match="message | arbitrary" mode="before-result">
  <tr>
    <td><xsl:value-of select="@level"/>A</td>
    <td>
      <xsl:choose>
        <xsl:when test="'arbitrary' = name()">
          <pre><font size="-1"><xsl:value-of select="."/></font></pre>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </tr>
</xsl:template>

<!-- Only selected from previous checkresult apply-templates -->
<xsl:template match="fileCheck" mode="before-result">
  <tr>
    <td><font size="-1">fileCheck</font></td>
    <td>
      <xsl:text>Actual file: </xsl:text><xsl:value-of select="@actual"/><br/>
      <pre><font size="-1"><xsl:value-of select="."/></font></pre>
    </td>
  </tr>
</xsl:template>

<!-- We implicitly skip all passes and non-related messages, etc. -->

<!-- Override default text node processing, so statistics, arbitrary messages, and other stuff is skipped -->
<xsl:template match="text()"/>
<xsl:template match="text()" mode="before-result"/>

</xsl:stylesheet>
