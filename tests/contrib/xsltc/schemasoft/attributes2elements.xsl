<?xml version="1.0"?>
<!-- Replace attributes with elements.
     Note that the reverse transform is not possible.

Copyright J.M. Vanel 2000 - under GNU public licence 
xt testHREF.xml attributes2elements.xslt > attributes2elements.xml

 -->

<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                version="1.0" >

 <xsl:template match ="@*" >
   <xsl:element name='{name()}'>
    <xsl:value-of select='.' />
   </xsl:element>
 </xsl:template>

 <xsl:template match="*">
  <xsl:copy>
    <xsl:apply-templates select="*|@*" />
  </xsl:copy>
 </xsl:template>

</xsl:stylesheet>


