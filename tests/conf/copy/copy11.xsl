<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: copy11 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.3 Using Values of Variables & Parameters with xsl:copy-of. -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Verify copy-of with a nodeset. -->

<xsl:template match="/">
  <root>
    <Out>
       <xsl:attribute name="attr1">
         <xsl:copy-of select="docs//a"/>
       </xsl:attribute>  
    </Out><xsl:text>&#010;</xsl:text>
    <xsl:copy-of select="docs//a"/>
  </root>
</xsl:template>

</xsl:stylesheet>