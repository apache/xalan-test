<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: string62 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions -->
  <!-- AdditionalSpec: http://www.w3.org/1999/11/REC-xpath-19991116-errata/ -->
  <!-- ErrataAdd: 20011101 -->
  <!-- Purpose: If the second argument to contains is the empty string, then true is returned. -->

<xsl:output method="xml" encoding="UTF-8" indent="no"/>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="contains('','')"/>
  </out>
</xsl:template>

</xsl:stylesheet>