<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="d.xsl"/>

<xsl:template match="author">
  B-author:  
  <xsl:apply-imports/><xsl:text>,</xsl:text>
</xsl:template>
   
</xsl:stylesheet>