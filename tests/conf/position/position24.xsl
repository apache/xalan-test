<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: POS24 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 4.1 -->
  <!-- Purpose: Test of 'last()' function with current context position. -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="*">
      <xsl:value-of select="last()=4"/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
