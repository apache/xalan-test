<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr13 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose: Try to set same in-template param twice -->
  <!-- ExpectedException: Duplicate variable declaration. -->
  <!-- Author: David Marston -->

<xsl:template match="doc">
  <xsl:param name="partest" select="contains('foo','of')"/>
  <xsl:param name="partest" select="contains('foo','o')"/>
  <out>
    <xsl:value-of select="$partest"/>
  </out>
</xsl:template>

</xsl:stylesheet>
