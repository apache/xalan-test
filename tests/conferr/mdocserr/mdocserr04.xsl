<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:ped="ped.com">

  <!-- FileName: MDOCSerr04 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2  -->
  <!-- Purpose: Top-level elements must have some namespace. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: top-level element test has no namespace -->
  <!-- ExpectedException: test is not allowed in this position in the stylesheet! -->

<xsl:template match="doc">
  <out><xsl:text>
</xsl:text>
    <xsl:copy-of select='document("")//test'/><xsl:text>
</xsl:text>
  </out>
</xsl:template>

<ped:test>Test1</ped:test>
<test>Test2</test>

</xsl:stylesheet>