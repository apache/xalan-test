<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATCHerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for error when xsl:template has neither match nor name. -->
  <!-- ExpectedException: xsl:template requires either a name or a match attribute. -->

<xsl:template match="letters">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template>
  <xsl:value-of select="."/>
  <xsl:text> Huh? </xsl:text>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>
