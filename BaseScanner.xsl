<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect"
    version="1.0">

<!-- FileName: BaseScanner.xsl -->
<!-- Author: shane_curcuru@us.ibm.com -->
<!-- Purpose: Base stylesheet for org.apache.qetest.XMLFileLogger
     logFiles that defines the structure of test output reports.
     Designed to be 'subclassed' by import/include and defining 
     custom output for your desired elements.
-->

<xsl:output method="html"
          doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

<lxslt:component prefix="redirect" elements="write open close" functions="">
    <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
</lxslt:component>  

<!-- Include constant definitions for results file elements, 
     attributes, and values, copied from relevant Java code -->
<xsl:include href="resultsConstants.xsl"/>

<!-- Name of file for mini-fails redirected output -->
<xsl:param name="redirectFilename">BaseScannerMini.html</xsl:param>

<!-- ================================== -->
<!-- Root template: just define the structure of the output.
     Allow subclasses to provide templates for each part.
-->
<xsl:template match="resultsfile">
  <html>
    <head>
      <title><xsl:text>Test Results file: </xsl:text><xsl:value-of select="@logFile"/></title>
    </head>
    <body>
      <p align="center" font="-2">
        <xsl:text>Original results.xml: </xsl:text>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="@logFile"/></xsl:attribute>
          <xsl:value-of select="@logFile"/>
        </xsl:element>
        <xsl:text>    Processing stylesheet: </xsl:text>
        <xsl:call-template name="printScannerName"/>
      </p>
      <!-- Handle each individual testfile (i.e. TestName.java) that ran. -->
      <xsl:apply-templates select="testfile"/>

    </body>
  </html>
</xsl:template>

<xsl:template name="printScannerName">
  <xsl:element name="a">
    <xsl:attribute name="href"><xsl:text>src/xsl/BaseScanner.xsl</xsl:text></xsl:attribute>
    <xsl:text>BaseScanner</xsl:text>
  </xsl:element>
</xsl:template>

<xsl:template match="testfile">
  <!-- Output any header info, like overall results summary -->
  <xsl:call-template name="test-header"/>

  <!-- Output detail results from testcases, etc. -->
  <xsl:call-template name="test-body"/>

  <!-- Output any footer info, like environment hashes, etc. -->
  <xsl:call-template name="test-footer"/>
</xsl:template>


<!-- ================================== -->
<!-- Structural templates: each named template is normally called 
     from the root template of / to perform it's part.
-->
<!-- Output any header info, like overall results summary -->
<xsl:template name="test-header">
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
    <!-- Print out mini-summary of overall results -->
    <tr>
      <td width="20%"><xsl:text>Overall (TestCases/TestPoints)</xsl:text></td>
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
    <tr>
      <td>
        <xsl:text>Testcases</xsl:text>
      </td>
      <td>
        <!-- Create links to non-passing testcases for easy navigation -->
        <xsl:text>Fails: </xsl:text>
        <xsl:for-each select="testcase[caseresult/@result=$FAIL]">
          <xsl:call-template name="create-testcase-link">
            <xsl:with-param name="testcase" select="."/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:text> Errors: </xsl:text>
        <xsl:for-each select="testcase[caseresult/@result=$ERRR]">
          <xsl:call-template name="create-testcase-link">
            <xsl:with-param name="testcase" select="."/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:text> Ambiguous: </xsl:text>
        <xsl:for-each select="testcase[caseresult/@result=$AMBG]">
          <xsl:call-template name="create-testcase-link">
            <xsl:with-param name="testcase" select="."/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:text> Incomplete: </xsl:text>
        <xsl:for-each select="testcase[caseresult/@result=$INCP]">
          <xsl:call-template name="create-testcase-link">
            <xsl:with-param name="testcase" select="."/>
          </xsl:call-template>
        </xsl:for-each>
      </td>
    </tr>
    <tr>
      <td>
        <!-- Create a link to the hashtables for this file -->
        <font size="-1">
          <xsl:element name="a">
            <xsl:attribute name="href"><xsl:value-of select="$hash-marker"/><xsl:value-of select="@filename"/></xsl:attribute>
            <xsl:text>System.properties, etc. during the test</xsl:text>
          </xsl:element>
        </font>
      </td>
    </tr>

  </table>

</xsl:template>

<!-- Output detail results from testcases, etc. -->
<xsl:template name="test-body">

  <!-- Process all testcases and interim messages -->
  <xsl:apply-templates select="*[not(name(.)='hashtable')]" />

</xsl:template>

<!-- Output any footer info, like environment hashes, etc. -->
<xsl:template name="test-footer">
  <!-- @todo optimize so anchor only appears if there are hashtables -->
  <br/>
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="$hash-marker"/><xsl:value-of select="@filename"/></xsl:attribute>
    <xsl:text>System properties follow for: </xsl:text><xsl:value-of select="@filename"/>
  </xsl:element>
  <!-- Dump hashtables at the file level, which are system properties & etc. -->
  <xsl:apply-templates select="hashtable"/>

</xsl:template>

<!-- 'Worker' template to convert loggingLevels -->
<!-- This is not terribly efficient, but it is prettier! -->
<xsl:template name="printLevel">
  <xsl:param name="level"></xsl:param>
  <xsl:choose>
    <xsl:when test="$ERRORMSG=$level"><xsl:text>error</xsl:text></xsl:when>
    <xsl:when test="$ERRORMSG=$level"><xsl:text>error</xsl:text></xsl:when>
    <xsl:when test="$FAILSONLY=$level"><xsl:text>failmsg</xsl:text></xsl:when>
    <xsl:when test="$WARNINGMSG=$level"><xsl:text>warning</xsl:text></xsl:when>
    <xsl:when test="$STATUSMSG=$level"><xsl:text>status</xsl:text></xsl:when>
    <xsl:when test="$INFOMSG=$level"><xsl:text>info</xsl:text></xsl:when>
    <xsl:when test="$TRACEMSG=$level"><xsl:text>trace</xsl:text></xsl:when>
    <xsl:otherwise><xsl:value-of select="$level"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ================================== -->
<!-- Element templates: output basic data for each of the common 
     test elements, like messages, checks, etc.
-->
<!-- Testfile is basically just a redirect to apply lower level templates -->
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
    <!-- Normally, process all sub-elements using the table mode -->
    <xsl:apply-templates mode="table"/>
    <tr>
      <td><br/><hr/></td>
      <td><xsl:value-of select="caseresult/@result"/>:<xsl:text>Testcase #</xsl:text><xsl:value-of select="@desc"/></td>
    </tr>
  </table>
</xsl:template>

<!-- Handle checkresult statements: each test point's result -->
<xsl:template match="checkresult" mode="table">
  <tr>
    <td>
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
</xsl:template>

<!-- Handle basic test messages: status, info, trace, etc. -->
<xsl:template match="message" mode="table">
  <tr>
    <td>
      <xsl:call-template name="printLevel">
        <xsl:with-param name="level" select="@level"/>
      </xsl:call-template>
    </td>
    <td><xsl:value-of select="."/></td>
  </tr>
</xsl:template>

<!-- Handle basic test messages when not in a testcase -->
<xsl:template match="message">
  <p>
    <xsl:call-template name="printLevel">
      <xsl:with-param name="level" select="@level"/>
    </xsl:call-template>
    <xsl:text> : </xsl:text><xsl:value-of select="."/>
  </p>
</xsl:template>

<!-- Handle arbitrary test messages: smaller font and preformatted -->
<xsl:template match="arbitrary" mode="table">
  <tr>
    <td>
      <xsl:call-template name="printLevel">
        <xsl:with-param name="level" select="@level"/>
      </xsl:call-template>
    </td>
    <td><font size="-1"><pre><xsl:value-of select="."/></pre></font></td>
  </tr>
</xsl:template>

<!-- Handle arbitrary test messages when not in a testcase -->
<xsl:template match="arbitrary">
  <p>
    <xsl:call-template name="printLevel">
      <xsl:with-param name="level" select="@level"/>
    </xsl:call-template>
    <xsl:text> : </xsl:text>
    <font size="-1"><pre><xsl:value-of select="."/></pre></font>
  </p>
</xsl:template>

<!-- Handle fileCheck elements outputting info about files, etc.
     Note that we copy the contents of the element, since some 
     tests might have element output here. 
-->
<xsl:template match="fileCheck" mode="table">
  <tr>
    <td><font size="-1">fileCheck</font></td>
    <td>
	  <xsl:text>Actual: </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@actual"/></xsl:attribute>
        <xsl:value-of select="@actual"/>
      </xsl:element><br/>
	  <xsl:text>Reference: </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@reference"/></xsl:attribute>
        <xsl:value-of select="@reference"/>
      </xsl:element><br/>
	  <br/>
      <pre><font size="-1"><xsl:copy-of select="."/></font></pre>
    </td>
  </tr>
</xsl:template>

<!-- Handle perf info simplistically; print out all attrs as timing data -->
<xsl:template match="perf" mode="table">
  <tr>
    <td><font size="-1">perf</font></td>
    <td>
      <xsl:text>[</xsl:text><xsl:value-of select="@idref"/><xsl:text>]</xsl:text><br/>
      <xsl:for-each select="attribute::*">
        <xsl:choose>
          <!-- Skip unneeded attrs -->
          <xsl:when test="'level' = name()"/>
          <xsl:when test="'testlet' = name()"/>
          <xsl:when test="'idref' = name()"/>
          <xsl:when test="'actual' = name()"/>
          <xsl:when test="'reference' = name()"/>
          <!-- Output any other perf-related attrs, whatever they are -->
          <xsl:otherwise>
            <xsl:value-of select="name()"/><xsl:text> = </xsl:text><xsl:value-of select="."/><br/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </td>
  </tr>
</xsl:template>

<!-- Handle basic hashtables and items within them -->
<xsl:template match="hashtable">
  <table border="1" width="80%" cellspacing="1" cellpadding="2">
    <caption><xsl:value-of select="@desc"/></caption>
    <xsl:apply-templates select="hashitem"/>
  </table>
</xsl:template>

<xsl:template match="hashitem">
  <tr>
    <td><xsl:value-of select="@key"/></td>
    <td>
      <!-- Integrate with S4JLoadUtil: create links to loaded schemas -->
      <xsl:choose>
        <xsl:when test="@key='current-file'">
          <xsl:element name="a">
            <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
            <xsl:value-of select="."/>
          </xsl:element>
        </xsl:when>
        <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
      </xsl:choose>
    </td>
  </tr>
</xsl:template>

<!-- Handle basic hashtables and items when in a table, which is a bit tricky -->
<xsl:template match="hashtable" mode="table">
  <tr>
  <td><xsl:text>hash</xsl:text></td>
    <td>
      <table border="1" width="80%" cellspacing="1" cellpadding="2">
        <caption><xsl:value-of select="@desc"/></caption>
        <xsl:apply-templates select="hashitem"/><!-- note no mode needed -->
      </table>
    </td>
  </tr>
</xsl:template>

<!-- Worker template to make testcase links: @desc is in anchor -->
<xsl:template name="create-testcase-link">
  <xsl:param name="testcase"></xsl:param>
  <font size="-1">
    <xsl:element name="a">
      <xsl:attribute name="href"><xsl:value-of select="$testcase-marker"/><xsl:value-of select="translate($testcase/@desc,'\ ','-')"/></xsl:attribute>
      <xsl:text>Testcase#</xsl:text><xsl:value-of select="substring-before($testcase/@desc,' ')"/>
    </xsl:element>
    <xsl:text>, </xsl:text>
  </font>
</xsl:template>

<!-- Worker template to make testcase anchors: @desc is in anchor -->
<xsl:template name="create-testcase-anchor">
  <xsl:param name="testcase"></xsl:param>
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="$testcase-marker"/><xsl:value-of select="translate($testcase/@desc,'\ ','-')"/></xsl:attribute>
    <xsl:value-of select="@desc"/>
  </xsl:element>
</xsl:template>

<!-- Worker template to make checkresult anchors: @result is in anchor -->
<xsl:template name="create-checkresult-anchor">
  <xsl:param name="checkresult"></xsl:param>
  <xsl:element name="a">
    <xsl:attribute name="name"><xsl:value-of select="$checkresult-marker"/><xsl:value-of select="translate($checkresult/@desc,'\ ','-')"/></xsl:attribute>
    <xsl:value-of select="@result"/>
  </xsl:element>
</xsl:template>

<!-- Override default text node processing, so statistics and other misc text is skipped -->
<xsl:template match="text()"/>
<xsl:template match="text()" mode="table"/>

</xsl:stylesheet>
