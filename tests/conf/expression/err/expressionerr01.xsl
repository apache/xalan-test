<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: expressionerr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 -->
  <!-- Purpose: Invoke unparsed-entity-uri function with zero arguments -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: The unparsed-entity-uri function should take one argument -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="unparsed-entity-uri()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
