<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOLerr05 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 3.4 -->
  <!-- Purpose: Test of invalid function that resembles "true" in = relation. -->
  <!-- ExpectedException: XSL Warning: Could not find function: troo -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="2 = troo()"/>
  </out>
</xsl:template>
 
</xsl:stylesheet>
