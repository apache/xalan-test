<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0">

  <!-- FileName: VERerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 Stylesheet element -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Don't allow xsl:transform inside the stylesheet element. -->
  <!-- ExpectedException: xsl:transform is not allowed in this position in the stylesheet -->

  <xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0">
    <xsl:template match="doc">
      <out>
        This is from the nested stylesheet
      </out>
    </xsl:template>

  </xsl:transform>

  <xsl:template match="doc">
    <out>
      Should have an error before this prints
    </out>
  </xsl:template>

</xsl:stylesheet>