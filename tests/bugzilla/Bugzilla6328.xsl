<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

<xsl:template match="/">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="item">
  <item>
    <xsl:attribute name="num"><xsl:value-of select="@num"/></xsl:attribute>
    <text><xsl:value-of select="."/></text>
    <normalized><xsl:value-of select="normalize-space(.)"/></normalized>
  </item>
</xsl:template>

</xsl:stylesheet>
