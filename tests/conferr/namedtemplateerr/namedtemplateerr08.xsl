<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 Named Templates -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put xsl:call-template at top level, which is illegal. -->
  <!-- ExpectedException: xsl:call-template is not allowed in this position in the stylesheet -->

<xsl:call-template name="ntmp1">
  <xsl:with-param name="pvar1" select="value"/>
</xsl:call-template>

<xsl:template name="ntmp1">
  <xsl:param name="pvar1">pvar1 default data</xsl:param>
  <xsl:param name="pvar2">pvar2 default data</xsl:param>
  <xsl:value-of select="$pvar1"/><xsl:text>,</xsl:text>
  <xsl:value-of select="$pvar2"/>
</xsl:template>

</xsl:stylesheet>
