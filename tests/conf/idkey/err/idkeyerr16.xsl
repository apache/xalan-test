<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr16 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 Miscellaneous Additional Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'generate-id()' with multiple arguments. Should generate
       an error. Should test for zero, or one argument.  -->
  <!-- ExpectedException: generate-id only allows 1 argument -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="generate-id(a,d)"/><xsl:text>,</xsl:text>
  </out>
</xsl:template>

</xsl:stylesheet>
