<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: MATHerr01 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.5 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test reaction to obsolete 'quo' operator. -->
  <!-- ExpectedException: Old syntax: quo(...) is no longer defined in XPath -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="6 quo 4"/>
  </out>
</xsl:template>

</xsl:stylesheet>
