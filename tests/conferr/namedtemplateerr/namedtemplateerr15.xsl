<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr15 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 6 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to set prefix part of QName to null string. -->
  <!-- ExpectedException: Name cannot start with a colon -->
  <!-- ExpectedException: A prefix of length 0 was detected -->
  <!-- ExpectedException: Template name is not a valid QName -->

<xsl:template match="doc">
  <out>
    <xsl:call-template name=":tmplt1">
      <xsl:with-param name="pvar1" select="doc/a"/>
    </xsl:call-template>
  </out>
</xsl:template>

<xsl:template name="tmplt1">
  <xsl:param name="pvar1">Default text in pvar1</xsl:param>
  <xsl:value-of select="$pvar1"/>
</xsl:template>

<xsl:template name=":tmplt1">
  <xsl:text>Empty-prefix template got called!</xsl:text>
</xsl:template>

</xsl:stylesheet>
