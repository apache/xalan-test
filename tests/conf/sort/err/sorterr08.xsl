<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:baz1="http://xsl.lotus.com/ns1">

  <!-- FileName: SORTerr08 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 10 -->
  <!-- Purpose: Undefined value for data-type attribute on sort, but it's qualified. -->
  <!-- ExpectedException: -->

<xsl:template match="doc">
  <out>
    <xsl:for-each select="t">
      <xsl:sort data-type="baz1:badtype"/>
      <xsl:value-of select="."/><xsl:text>|</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
