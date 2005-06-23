<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:lowest() -->

<xsl:template match="/">
  <out>
     This is for testing the node-set of b with lowest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:lowest(//b))"/>
     This is for testing the node-set of c with lowest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:lowest(//c))"/>
     This is for testing the node-set of d with lowest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:lowest(//d))"/>
  </out>   

</xsl:template>

</xsl:stylesheet>