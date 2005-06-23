<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:log() -->

<xsl:template match="/">
  <out>
     This is for testing the Euler constant
     <xsl:for-each select="doc/child::*">
       <xsl:param name="val" select="."/>
       <xsl:text>log of $val =</xsl:text>
       <xsl:value-of select="math:log($val)"/>
       <br/>
     </xsl:for-each>
  </out>   

</xsl:template>

</xsl:stylesheet>