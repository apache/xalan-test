<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Attempt to use xsl:if inside xsl:attribute-set -->
  <!-- ExpectedException: xsl:if is not allowed in this position in the stylesheet! -->

<xsl:output method="xml" encoding="UTF-8" indent="yes" />

<xsl:attribute-set name="set1">
  <xsl:attribute name="color">black</xsl:attribute>
  <xsl:if test="32 &gt; 1">
    <xsl:attribute name="font-size">14pt</xsl:attribute>
  </xsl:if>
</xsl:attribute-set>

<xsl:template match="/">
  <out>
    <test1 xsl:use-attribute-sets="set1"></test1>
  </out>
</xsl:template>

</xsl:stylesheet>