<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: TransformerAPIHTMLFormat.xsl -->
  <!-- Purpose: Legal HTML output for use with OutputPropertiesTest.java -->

<!-- Include various XSLT spec xsl:output attrs -->
<xsl:output method="html"
            media-type="text/test/xml"
            omit-xml-declaration="yes" />

<xsl:template match="/">
  <HTML>
  <xsl:apply-templates/>
  </HTML>
</xsl:template>

<xsl:template match="html-tag">
    <HEAD>
      <xsl:element name="TITLE"><xsl:value-of select="head-tag/title-tag/@text"/></xsl:element>
      <xsl:text>xsl:text within HEAD tag</xsl:text>
    </HEAD>
    <BODY>
    <xsl:apply-templates select="body-tag"/>
    <xsl:text disable-output-escaping="yes">&lt;P>Fake 'p' element&lt;/P></xsl:text>
    <!-- Some HTML elements below, just for fun -->
    <P>&#064; &#160; &#126; &#169; &#200;</P>
    </BODY>
</xsl:template>

<xsl:template match="body-tag">
    <xsl:apply-templates select="p-tag | ul-tag"/>
</xsl:template>

<xsl:template match="p-tag">
  <xsl:element name="P">
    <xsl:value-of select="."/>
    <xsl:apply-templates select="br-tag | hr-tag"/>
  </xsl:element>
</xsl:template>
 
<xsl:template match="ul-tag">
  <UL>
    <xsl:copy-of select="."/>
  </UL>
</xsl:template>

<xsl:template match="br-tag">
  <BR/>
</xsl:template>

<xsl:template match="hr-tag">
  <HR></HR>
</xsl:template>


</xsl:stylesheet>
