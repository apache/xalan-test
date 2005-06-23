<?xml version="1.0"?> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:common="http://exslt.org/common" 
exclude-result-prefixes="common">

<!-- Test exslt:node-set applied to a result tree fragment -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="count(common:node-set(//*))"/>    
  </out>
</xsl:template>
 
</xsl:stylesheet>
