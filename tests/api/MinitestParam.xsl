<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text" />

<!-- Note: params must match various projects' Minitest.java tests -->
<!-- params with select=value -->
  <xsl:param name="param1s" select="'default1s'"/>
  <xsl:param name="param2s" select="'default2s'"/>
  <xsl:param name="param3s" select="default3s"/>

<!-- params with node values -->
  <xsl:param name="param1n">'default1n'</xsl:param>
  <xsl:param name="param2n">'default2n'</xsl:param>
  <xsl:param name="param3n">default3n</xsl:param>

  <xsl:template match="doc">
    <out>
      <xsl:text>&#10;</xsl:text>
      <xsl:text> :param1s: </xsl:text><xsl:value-of select="$param1s"/>
      <xsl:text> :param2s: </xsl:text><xsl:value-of select="$param2s"/>
      <xsl:text> :param3s: </xsl:text><xsl:value-of select="$param3s"/>
      <xsl:text>&#10;</xsl:text>
      <xsl:text> :param1n: </xsl:text><xsl:value-of select="$param1n"/>
      <xsl:text> :param2n: </xsl:text><xsl:value-of select="$param2n"/>
      <xsl:text> :param3n: </xsl:text><xsl:value-of select="$param3n"/>
      <xsl:text>&#10;</xsl:text>
    </out>
  </xsl:template>
</xsl:stylesheet>
