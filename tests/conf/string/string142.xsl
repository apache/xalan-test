<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: string142 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions -->
  <!-- AdditionalSpec: http://www.w3.org/1999/11/REC-xpath-19991116-errata/ -->
  <!-- ErrataAdd: 20011101 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: If the second argument to substring-before is the empty string, then the empty string is returned. -->

<xsl:output method="xml" encoding="UTF-8" indent="no"/>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="string-length(substring-before('abcde',''))"/>
  </out>
</xsl:template>

</xsl:stylesheet>