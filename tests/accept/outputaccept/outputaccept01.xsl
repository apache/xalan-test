<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: outputaccept01 -->
  <!-- Document: http://www.w3.org/TR/Xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.3 Node Sets -->
  <!-- Purpose: NodeSet union using the attribute axis -->
  <!-- Creator: Carmelo Montanez --><!-- Expression006 in NIST suite -->

<xsl:template match="/">
  <out><xsl:text>
</xsl:text>
    <xsl:for-each select="doc">
      <xsl:apply-templates select="attribute::attr1|attribute::attr2"/>
    </xsl:for-each>
    <xsl:text>
</xsl:text></out>
</xsl:template>

<xsl:template match="*">
  <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>

