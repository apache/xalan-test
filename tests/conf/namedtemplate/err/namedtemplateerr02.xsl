<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: namedtemplateerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.6 -->
  <!-- Purpose: Error test- parameter of outer template unknown inside inner template. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: VariableReference given for variable out of context or without definition! -->

<xsl:template match="doc">
  <out>
    <xsl:call-template name="tmplt1">
      <xsl:with-param name="pvar1" select="555"/>
    </xsl:call-template>
<xsl:text>
Back to main template.</xsl:text>
  </out>
</xsl:template>

<xsl:template name="tmplt1">
  <xsl:param name="pvar1">pvar1 default data</xsl:param>
  <!-- pvar1 won't be in scope in next template, so pass it in via new variable. -->
  <xsl:variable name="passto2">
    <xsl:value-of select="number($pvar1)"/>
  </xsl:variable>

  <xsl:value-of select="$passto2"/><xsl:text>, </xsl:text>
  <xsl:call-template name="tmplt2">
    <xsl:with-param name="t1num" select="$passto2"/>
  </xsl:call-template>
  <xsl:text>
Back to template 1.</xsl:text>
</xsl:template>

<xsl:template name="tmplt2">
  <xsl:param name="t1num">t1num default data</xsl:param>
  <xsl:value-of select="444 + $t1num"/><xsl:text>, prior item should be 999, </xsl:text>
  <xsl:value-of select="222 + $pvar1"/><xsl:text>, prior item should be 777</xsl:text>
</xsl:template>

</xsl:stylesheet>
