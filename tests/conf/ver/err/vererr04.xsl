<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0">

  <!-- FileName: VERerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 Stylesheet element -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Don't allow xsl:stylesheet inside the stylesheet element. -->
  <!-- ExpectedException: cannot nest one stylesheet/transform inside another -->

  <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0">
    <xsl:template match="doc">
      <out>
        This is from the nested stylesheet
      </out>
    </xsl:template>

  </xsl:stylesheet>

  <xsl:template match="doc">
    <out>
      Should have an error before this prints
    </out>
  </xsl:template>

</xsl:stylesheet>