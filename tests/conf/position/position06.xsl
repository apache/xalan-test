<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: POS06 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 4.1 -->
  <!-- Purpose: Test of 'count()'. -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="count(*)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
