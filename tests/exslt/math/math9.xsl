<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:highest() -->

<xsl:template match="doc">
  <out>
     This is for testing the node-set of doc/b with highest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:highest(b))"/>
     This is for testing the node-set of b1/b with highest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:highest(//b1/b))"/>
     This is for testing the node-set of b2/b with highest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:highest(//b2/b))"/>
     This is for testing the node-set of b3/b with highest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:highest(b3/b))"/>
     This is for testing the node-set of //b with highest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:highest(//b))"/>
     
     This is for testing the node-set of c with highest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:highest(//c))"/>
     This is for testing the node-set of d with highest value in it.  That tree would be:
     <xsl:value-of select="math:max(math:highest(//d))"/>
  </out>   

</xsl:template>

</xsl:stylesheet>