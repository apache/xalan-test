<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: expression02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 -->
  <!-- Purpose: Invoke unparsed-entity-uri function -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="unparsed-entity-uri('hatch-pic')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
