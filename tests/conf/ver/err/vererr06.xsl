<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0">

  <!-- FileName: VERerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 Stylesheet element -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Don't allow xsl:stylesheet inside a template. -->
  <!-- ExpectedException: Cannot nest stylesheet/transform inside other elements -->

<xsl:template match="doc">
  <out>
    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
      <xsl:text>This is from the nested stylesheet</xsl:text>
    </xsl:stylesheet>
  </out>
</xsl:template>

</xsl:stylesheet>
