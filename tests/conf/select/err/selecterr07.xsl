<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: selecterr07 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.7 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: NCName followed by ( must be recognized as a NodeType
      or FunctionName. -->
  <!-- ExpectedException: Could not find function: c -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="c()">
      <xsl:copy/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
