<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: STR131 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Purpose: Test of 'string()' with zero arguments. Context node just has one text child. -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="string()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
