<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="e.xsl"/>

<xsl:template match="title">
  C-title:<xsl:text>,</xsl:text>
  <xsl:apply-imports/>
</xsl:template>
  
<xsl:template match="chapters">
  C-chapters:
  <xsl:apply-imports/>
</xsl:template> 

     
</xsl:stylesheet>
