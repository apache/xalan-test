<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: position97 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.1 -->
  <!-- Creator: Joerg Heinicke, trimmed by David Marston -->
  <!-- Purpose: Use a (number-valued) variable in a predicate, but inside a for-each. -->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:template match="root">
  <out>
  <xsl:text>
</xsl:text>
    <xsl:apply-templates select="base//description"/>
  </out>
</xsl:template>
  
<xsl:template match="description">
  <xsl:variable name="pos" select="position()"/>
  <tr row="{$pos}">
    <xsl:for-each select="/root/base">
      <td>
        <xsl:copy-of select="@side"/>
        <xsl:value-of select=".//description[$pos]"/>
      </td>
    </xsl:for-each>
  </tr>
  <xsl:text>
</xsl:text>
</xsl:template>

</xsl:stylesheet>
