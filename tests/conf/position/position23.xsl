<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: POS23 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.4 -->
  <!-- Purpose: Test of positional indexing on the text() nodes. -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="text()[3]"/>
  </out>
</xsl:template>

</xsl:stylesheet>
