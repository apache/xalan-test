<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr19 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Show what happens if there are any literal characters between
       two groups of zero digits. -->
  <!-- ExpectedException: java.lang.RuntimeException: Malformed format string -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(90232.0884,'000000zip000000')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
