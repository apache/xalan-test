<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- From the XSLT spec: "the identity transformation can be written using xsl:copy as follows:" -->
  <xsl:template match="@*|node()">
    <embedded-reuse-test>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
    </embedded-reuse-test>
  </xsl:template>
     
</xsl:stylesheet>
