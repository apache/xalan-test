<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr20 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Put grouping separator adjacent to decimal-separator. -->
  <!-- ExpectedException: java.lang.RuntimeException: Malformed format string -->
  <!-- ExpectedException: Malformed format string: ######,.00 -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number(90232.0884,'######,.00')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
