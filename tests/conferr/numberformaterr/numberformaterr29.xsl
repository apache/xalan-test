<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr29 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of format-number with too many arguments. -->
  <!-- ExpectedException: format-number() must have at most 3 arguments -->
  <!-- ExpectedException: only allows 2 or 3 arguments -->

<xsl:decimal-format name="myminus" minus-sign='_' />

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(-2392.14*36.58,'#####0.000###','myminus',3407)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
