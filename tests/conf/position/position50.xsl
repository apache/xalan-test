<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: POS50 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 4.1 -->
  <!-- Purpose: Test of 'position()' when used with xsl:key and xsl:apply-templates. -->

<xsl:key name="k" match="a" use="@test"/>
<xsl:key name="k2" match="a" use="num"/>

<xsl:template match="doc">
  <out>
    <xsl:apply-templates select="key('k','true')[3]/num"/>
  </out>
</xsl:template>

<xsl:template match="num">
  <B><xsl:value-of select="."/></B>
</xsl:template>

</xsl:stylesheet>
