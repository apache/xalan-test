<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: EXTENDerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 14 Extensions -->
  <!-- Purpose: Test element-available with too few arguments. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: element-available requires one argument -->

<xsl:template match="/">
  <out>
     <xsl:value-of select="element-available()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
