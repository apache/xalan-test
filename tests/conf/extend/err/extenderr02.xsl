<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: EXTENDerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 14 Extensions -->
  <!-- Purpose: Test function-available with too few arguments. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: function-available requires one argument -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="function-available()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
