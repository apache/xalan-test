<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: stringerr13 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'substring()' with too many arguments -->
  <!-- ExpectedException: substring() has too many arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="substring('ENCYCLOPEDIA',4,5,2)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
