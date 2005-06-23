<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:random() -->

<xsl:template match="/">
   <out>
   <xsl:value-of select="math:random()"/>
   </out>
</xsl:template>

</xsl:stylesheet>