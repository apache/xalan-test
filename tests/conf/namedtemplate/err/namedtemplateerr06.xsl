<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to do call-template without its name attribute. -->
  <!-- ExpectedException: ??? -->

<xsl:template match="doc">
  <out>
    <xsl:call-template>
      <xsl:with-param name="pvar1" select="doc/a"/>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="tmplt1" match="doc/doc">
  <xsl:param name="pvar1">Default text in pvar1</xsl:param>
  <xsl:value-of select="$pvar1"/>
</xsl:template>

</xsl:stylesheet>
