<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: Attempt to write an attribute with no element -->
  <!-- Purpose: Try to create an attribute before there is any element produced. -->

<xsl:output method="xml" encoding="UTF-8"/>

<xsl:template match="/">
  <xsl:attribute name="baaad"><xsl:value-of select="doc/@top"/></xsl:attribute>
</xsl:template>

</xsl:stylesheet>