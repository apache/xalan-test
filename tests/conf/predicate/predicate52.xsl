<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- FileName: predicate52 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: Test ((a and b) or (c and d)) combination of logical expressions in a predicate -->
  <!-- Author: Morten Jorgensen -->

<xsl:template match="foo">

  <xsl:text>Test:  ((a and b) or (c and d)):&#xa;</xsl:text>
  <xsl:text>Match: </xsl:text>
  <xsl:for-each select="bar[(@a='1' and @b='1') or (@c='1' and @d='1')]">
    <xsl:value-of select="@seq"/><xsl:text> </xsl:text>
  </xsl:for-each>
  <xsl:text>&#xa;</xsl:text>

</xsl:template>

</xsl:stylesheet>
