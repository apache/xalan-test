<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: BOOL43 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 4.3 -->
  <!-- Purpose: Test of boolean() function, converting an empty result tree fragment -->

<xsl:variable name="emptyResultTreeFragTest">
  <xsl:value-of select="foo"/>
</xsl:variable>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="boolean($emptyResultTreeFragTest)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
