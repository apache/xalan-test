<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr18 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to select a '//' without a following node-test. -->
  <!-- ExpectedException: Abbreviation '//' must be followed by a step expression -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="//subitem">
      <xsl:value-of select="//"/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>