<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">
<!-- Indent just for readability -->    
<xsl:output indent="yes"/>

  <!-- FileName: libraryTokenize01.xsl -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Creator: Shane Curcuru -->
  <!-- Purpose: Basic test of tokenize() extension function -->

<xsl:template match="/">
  <out>
    <!-- Using copy-of then value-of on the same select= shows that 
         it's properly returning a node-set each time.
           copy-of will print out all the nodes' text values in the set
           value-of will only print the first nodes' text value
    -->
    <test desc="simple string, default separators">
      <xsl:copy-of select="xalan:tokenize('one  two&#x09;three')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:tokenize('one  two&#x09;three')"/>
    </test>
    <test desc="simple string, colon separator">
      <xsl:copy-of select="xalan:tokenize('one  two:three', ':')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:tokenize('one  two:three', ':')"/>
    </test>
    <test desc="blank string, default separators">
      <xsl:copy-of select="xalan:tokenize('')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:tokenize('')"/>
    </test>
    <test desc="blank string, blank separators">
      <xsl:copy-of select="xalan:tokenize('', '')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:tokenize('', '')"/>
    </test>
    <test desc="blank string, colon, x are separators">
      <xsl:copy-of select="xalan:tokenize('', ':x')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:tokenize('', ':x')"/>
    </test>
    <test desc="all delimiter string, default separators">
      <xsl:copy-of select="xalan:tokenize(' &#x09; 
')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:tokenize(' &#x09; 
')"/>
    </test>
    <test desc="all delimiter string, colon, x are separators">
      <xsl:copy-of select="xalan:tokenize(':x::xx:', ':x')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:tokenize(':x::xx:', ':x')"/>
    </test>
    <!-- Note: null string xalan:tokenize() is not a legal extension call -->
  </out>
</xsl:template>

</xsl:stylesheet>