<?xml version="1.0"?>
<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
   xmlns:em="http://www.psol.com/xtension/1.0">

  <!-- FileName: NSPCerr10 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for meaningful message when attempting to use namespace wildcard (*) -->
  <!-- ExpectedException: XSLT: ElemTemplateElement error: Can not resolve namespace prefix: -->
  <!-- ExpectedException: Prefix must resolve to a namespace: -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="*:foo">
  <in>
    <xsl:value-of select="."/>
  </in>
</xsl:template>

</xsl:stylesheet>
