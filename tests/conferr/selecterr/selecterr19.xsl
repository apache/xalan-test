<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr19 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to put '//' at end of path. -->
  <!-- ExpectedException: Abbreviation '//' must be followed by a step expression -->
  <!-- ExpectedException: Unexpected token -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="foo">
      <xsl:value-of select="item//"/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>