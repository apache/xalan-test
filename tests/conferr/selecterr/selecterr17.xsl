<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr17 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to follow '//' by a predicate, without node-test. -->
  <!-- ExpectedException: Found '[' without node test -->
  <!-- ExpectedException: Unexpected token -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="foo">
      <xsl:value-of select="item//[@type='x']"/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>