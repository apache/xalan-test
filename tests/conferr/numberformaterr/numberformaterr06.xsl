<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Show what happens if there are any filler digits (#) between
       zero-digits and the decimal-separator on the left. -->
  <!-- ExpectedException: java.lang.RuntimeException: Malformed format string -->
  <!-- ExpectedException: Malformed format string: 00,000,###.000### -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(4030201.050607,'00,000,###.000###')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
