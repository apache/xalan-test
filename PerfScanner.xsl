<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="1.0">
<xsl:output method="html"
          doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

<!-- FileName: FailScanner.xsl -->
<!-- Author: shane_curcuru@us.ibm.com -->
<!-- Purpose: Simple viewer for org.apache.qetest.XMLFileLogger
     logFile failing-only results put into simple HTML pages -->

<!-- Include constant definitions for results file elements, 
     attributes, and values, copied from relevant Java code -->
<xsl:include href="resultsConstants.xsl"/>

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

<!-- Output all testcases with minimal info in them -->
<xsl:template match="testcase">
    <tr>
      <td><br/><hr/></td>
      <td><xsl:text>Testcase #</xsl:text><xsl:value-of select="@desc"/><xsl:text> is </xsl:text><xsl:value-of select="caseresult/@result"/></td>
    </tr>
    <!-- Only select results; we only have matches later for failed results -->
    <xsl:apply-templates select="checkresult | perf | fileCheck" />
</xsl:template>

<!-- Printout basic pass/fail for each test -->
<xsl:template match="checkresult">
  <tr>
    <td><xsl:value-of select="@result"/></td>
    <td>
      <xsl:if test="@id">
        <xsl:text>[</xsl:text><xsl:value-of select="@id"/><xsl:text>] </xsl:text>
      </xsl:if>
      <xsl:value-of select="@desc"/>
    </td>
  </tr>
</xsl:template>

<!-- Print out simple block of perf info -->
<xsl:template match="perf">
  <tr>
    <td><b><xsl:value-of select="@idref"/></b></td>
    <td>
    <xsl:value-of select="@processor"/><br/>
    <xsl:if test="@avgparsexsl"><xsl:text> avgparsexsl = </xsl:text><xsl:value-of select="@avgparsexsl"/></xsl:if>
    <xsl:if test="@singletransform"><xsl:text> singletransform = </xsl:text><xsl:value-of select="@singletransform"/></xsl:if>
    <xsl:if test="@iterations"><xsl:text> iterations = </xsl:text><xsl:value-of select="@iterations"/></xsl:if>
    <xsl:if test="@unparsedxml"><xsl:text> unparsedxml = </xsl:text><xsl:value-of select="@unparsedxml"/></xsl:if>
    <xsl:if test="@parsexsl"><xsl:text> parsexsl = </xsl:text><xsl:value-of select="@parsexsl"/></xsl:if>
    <xsl:if test="@avgetoe"><xsl:text> avgetoe = </xsl:text><xsl:value-of select="@avgetoe"/></xsl:if>
    <xsl:if test="@avgunparsedxml"><xsl:text> avgunparsedxml = </xsl:text><xsl:value-of select="@avgunparsedxml"/></xsl:if>
    <xsl:if test="@etoe"><xsl:text> etoe = </xsl:text><xsl:value-of select="@etoe"/></xsl:if>
    <xsl:if test="@OVERALL"><xsl:text> OVERALL = </xsl:text><xsl:value-of select="@OVERALL"/></xsl:if>
    <xsl:if test="@XSLREAD"><xsl:text> XSLREAD = </xsl:text><xsl:value-of select="@XSLREAD"/></xsl:if>
    <xsl:if test="@XSLBUILD"><xsl:text> XSLBUILD = </xsl:text><xsl:value-of select="@XSLBUILD"/></xsl:if>
    <xsl:if test="@XMLREAD"><xsl:text> XMLREAD = </xsl:text><xsl:value-of select="@XMLREAD"/></xsl:if>
    <xsl:if test="@XMLBUILD"><xsl:text> XMLBUILD = </xsl:text><xsl:value-of select="@XMLBUILD"/></xsl:if>
    <xsl:if test="@TRANSFORM"><xsl:text> TRANSFORM = </xsl:text><xsl:value-of select="@TRANSFORM"/></xsl:if>
    <xsl:if test="@RESULTWRITE"><xsl:text> RESULTWRITE = </xsl:text><xsl:value-of select="@RESULTWRITE"/></xsl:if>
    <xsl:if test="@FIRSTLATENCY"><xsl:text> FIRSTLATENCY = </xsl:text><xsl:value-of select="@FIRSTLATENCY"/></xsl:if>
    </td>
  </tr>
</xsl:template>

<!-- Only selected from previous checkresult apply-templates -->
<xsl:template match="fileCheck">
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

</xsl:stylesheet>
