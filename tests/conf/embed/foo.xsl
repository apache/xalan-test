<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- FileName: foo.xsl -->
<!-- Document: http://www.w3.org/TR/xslt -->
<!-- DocVersion: 19991116 -->
<!-- Section: 2.7 -->
<!-- Purpose: General test of embedded stylesheet using Processing Instruction
              xsl:stylesheet.  -->

<xsl:template match="/">
  <out>
     <xsl:value-of select="doc/body/para"/>
  </out><xsl:text>&#10;</xsl:text>
</xsl:template>

</xsl:stylesheet>
