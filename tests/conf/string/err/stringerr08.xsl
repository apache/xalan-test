<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: STRerr08 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Purpose: Test of 'substring-before()' with one argument -->
  <!-- ExpectedException: substring-before() requires two arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="substring-before('ENCYCLOPEDIA')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
