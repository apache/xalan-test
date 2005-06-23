<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:min() -->

<xsl:template match="doc">
  <out>
     This is for finding the smallest value in the node-set b and printing it:
     <xsl:value-of select="math:min(//b)"/>
     This is for finding the smallest value in the node-set c and printing it:
     <xsl:value-of select="math:min(//c)"/>
     This is for finding the smallest value in the node-set d and printing it:
     <xsl:value-of select="math:min(//d)"/>
  </out>   

</xsl:template>

</xsl:stylesheet>