<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: SORTerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test use of xsl:sort inside a directive where it's not allowed. -->
  <!-- ExpectedException: xsl:sort can only be used with xsl:apply-templates or xsl:for-each. -->
  <!-- ExpectedException: (StylesheetHandler) xsl:sort can only be used with xsl:apply-templates or xsl:for-each. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:sort can only be used with xsl:apply-templates or xsl:for-each. -->

<xsl:template match="/">
  <Out>
    <xsl:comment>
      <xsl:sort/>
      <xsl:text>Comment content</xsl:text>
    </xsl:comment>
  </Out>
</xsl:template>

</xsl:stylesheet>