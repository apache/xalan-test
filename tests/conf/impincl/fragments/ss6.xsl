<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:param name="qParam" select="string('!From ss6!')"/>

<xsl:template match="price">
  q-price: 
  <xsl:value-of select="."/>
</xsl:template>
   
</xsl:stylesheet>
