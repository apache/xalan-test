<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- FileName: VERerr09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test that version number is required. -->
  <!-- ExpectedException: stylesheet must have version attribute -->
  <!-- ExpectedException: xsl:stylesheet requires attribute: version -->

<xsl:template match="/">
  <out>
    Choosing, based on value of version property.
    <xsl:choose>
      <xsl:when test="system-property('xsl:version') &gt;= 1.0">
        We are ready to use the 1.0 feature.
      </xsl:when>
      <xsl:otherwise>
        We didn't try to use the 1.0 feature, but we should have.
        <xsl:message>This stylesheet requires XSLT 1.0 or higher</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </out>
</xsl:template>

</xsl:stylesheet>
