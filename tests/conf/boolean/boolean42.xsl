<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOL42 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 4.3 -->
  <!-- Purpose: Test of boolean() function, converting a result tree fragment -->

<xsl:variable name="ResultTreeFragTest">
  <xsl:value-of select="doc"/>
</xsl:variable>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="boolean($ResultTreeFragTest)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
