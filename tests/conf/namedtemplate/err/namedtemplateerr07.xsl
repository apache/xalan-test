<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 Named Templates -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test with-param lacking name attribute. -->
  <!-- ExpectedException: XSL Error: xsl:with-param must have a 'name' attribute. -->
  <!-- ExpectedException: xsl:with-param requires attribute: name -->

<xsl:template match="doc">
  <out>
    <xsl:variable name="RTF">
      <xsl:value-of select="a"/>
    </xsl:variable>
    <xsl:call-template name="ntmp1">
      <xsl:with-param select="$RTF"/>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="ntmp1">
  <xsl:param name="pvar1">default data</xsl:param>
  <xsl:value-of select="$pvar1"/><xsl:text>,</xsl:text>
</xsl:template>

</xsl:stylesheet>
