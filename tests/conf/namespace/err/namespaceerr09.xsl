<?xml version="1.0"?>
<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
   xmlns:em="http://www.psol.com/xtension/1.0">

  <!-- FileName: namespaceerr09 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test for meaningful message when expression begins with : (has null namespace) -->
  <!-- ExpectedException: Extra illegal tokens: 'foo' -->

<xsl:template match = "doc">
  <out>
    <xsl:for-each select=":foo">
      <xsl:value-of select="."/>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
