<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                xmlns:common="http://exslt.org/common"
                extension-element-prefixes="math">

<!-- Test math:max() -->

<xsl:template match="doc">
  <out>
     This is for finding the maximum value in the node-set b and printing it:
     <xsl:value-of select="math:max(//b)"/>
     This is for finding the maximum value in the node-set c and printing it:
     <xsl:value-of select="math:max(//c)"/>
     This is for finding the maximum value in the node-set d and printing it:
     <xsl:value-of select="math:max(//d)"/>
     
  </out>   

</xsl:template>

</xsl:stylesheet>