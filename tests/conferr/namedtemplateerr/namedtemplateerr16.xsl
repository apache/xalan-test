<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr16 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Call a template using invalid name. -->
  <!-- ExpectedException: Invalid template name -->
  <!-- ExpectedException: xsl:call-template has an invalid 'name' attribute -->
  <!-- ExpectedException: Illegal value: tmplt1@bar used for QNAME attribute: name -->

<xsl:template match="doc">
  <out>
    <xsl:call-template name="tmplt1@bar">
      <xsl:with-param name="pvar1" select="doc/a"/>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="tmplt1">
  <xsl:param name="pvar1">Default text in pvar1</xsl:param>
  <xsl:value-of select="$pvar1"/>
</xsl:template>

<xsl:template name="tmplt1@bar">
  <xsl:text>Badly named template got called!</xsl:text>
</xsl:template>

</xsl:stylesheet>
