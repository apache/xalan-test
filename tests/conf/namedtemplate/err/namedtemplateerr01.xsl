<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test what happens with badly-formed select pattern in with-param. -->
  <!-- ExpectedException: Extra illegal tokens -->

<xsl:template match="doc">
  <out>
    <xsl:call-template name="tmplt1">
      <xsl:with-param name="pvar1" select="doc/[@place='below']"/>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="tmplt1" match="doc/doc">
  <xsl:param name="pvar1">Default text in pvar1</xsl:param>
  <xsl:value-of select="$pvar1"/>
</xsl:template>

</xsl:stylesheet>
