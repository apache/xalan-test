<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:foo="foo" >

  <xsl:template match="foo:bar">
    <out><xsl:apply-templates /></out>
  </xsl:template>

</xsl:stylesheet>
