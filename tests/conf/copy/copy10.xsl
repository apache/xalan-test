<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: copy10 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.3 Using Values of Variables & Parameters with xsl:copy-of. -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Verify copy-of with a result tree fragment. Copy to element and attribute. -->

<xsl:template match="/">
  <root>
   <Out>
      <xsl:attribute name="attr1">
        <xsl:copy-of select="docs//d"/>
      </xsl:attribute>  
   </Out><xsl:text>&#010;</xsl:text>
   <xsl:copy-of select="docs//d"/>
  </root>
</xsl:template>

</xsl:stylesheet>


