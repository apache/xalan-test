<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: matcherr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for error when xsl:template is nested in another top-level element. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: (StylesheetHandler) xsl:template is not allowed inside a template! -->
  <!-- Author: David Marston -->
  <!-- ExpectedException: xsl:template is not allowed in this position in the stylesheet! -->

<xsl:param name="bad">
  <xsl:template match="letters">
    <out>
      <xsl:apply-templates/>
    </out>
  </xsl:template>
</xsl:param>

<xsl:template match="text()"/>

</xsl:stylesheet>
