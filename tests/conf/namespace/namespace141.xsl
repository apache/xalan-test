<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namespace141 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements -->
  <!-- Creator: Gordon Chiu -->
  <!-- Purpose: Test for resetting of an unspecified default namespace by copy-of. -->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:template match = "/">
  <!-- create an RTF with no namespace nodes -->
  <!-- extended variant of namespace137 to check special cases -->
  <xsl:variable name="x">
    <xsl:element name="hello"/>
    <xsl:element name="hello2" namespace="http://literalURI">
      <xsl:element name="hello3" namespace=""/>
    </xsl:element>
    <xsl:element name="hello2" namespace="http://literalURI2"/>
    <xsl:element name="hello2">
      <xsl:element name="hello3" namespace=""/>
    </xsl:element>
  </xsl:variable>
  <xsl:variable name="y">
    <xsl:element name="hello"/>
  </xsl:variable>
  <out>
    <xsl:element name="literalName" namespace="http://literalURI">
      <xsl:copy-of select="$x"/>
    </xsl:element>
  </out>
  <out>
    <xsl:element name="literalName">
      <xsl:copy-of select="$y"/>
    </xsl:element>
  </out>
</xsl:template>

</xsl:stylesheet>
