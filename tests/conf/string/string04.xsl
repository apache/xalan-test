<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: str04 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Purpose: Test of 'string-length()' w/no arguments, and w/
       specific context node.  -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="string-length()"/><xsl:text> </xsl:text>
	<xsl:value-of select="string-length(doc/a)"/>
  </out>
</xsl:template>
 
</xsl:stylesheet>
