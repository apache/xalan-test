<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: VERerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.5 Forwards-Compatible Processing  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test validation when version matches supported version. -->
  <!-- ExpectedException: Unknown XSL element: exciting-new-8.5-feature -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Unknown XSL element: exciting-new-8.5-feature -->
  <xsl:template match="/">
    <out>
      Choosing, based on value of version property.
      <xsl:choose>
        <xsl:when test="system-property('xsl:version') &gt;= 1.0">
          We are trying to use the 8.5 feature in an earlier version.
          <xsl:exciting-new-8.5-feature/>
        </xsl:when>
        <xsl:otherwise>
          We didn't try to use the 8.5 feature, but we should have.
          <xsl:message>This stylesheet requires XSLT 1.0 or higher</xsl:message>
        </xsl:otherwise>
      </xsl:choose>
    </out>
  </xsl:template>

</xsl:stylesheet>
