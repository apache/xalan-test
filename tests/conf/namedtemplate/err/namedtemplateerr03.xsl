<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 -->
  <!-- Purpose: Try to do call-template without a template of the specified name. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: Could not find template named -->

<xsl:template match="doc">
  <out>
    <xsl:call-template name="tmplt2">
      <xsl:with-param name="pvar1" select="doc/a"/>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="tmplt1" match="doc/doc">
  <xsl:param name="pvar1">Default text in pvar1</xsl:param>
  <xsl:value-of select="$pvar1"/>
</xsl:template>

</xsl:stylesheet>
