<?xml version="1.1" ?>

  <!-- FileName: spec1103.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                To ensure that Control Characters in C1 range are output as
                Numeric Character Reference (NCR).
  -->
  
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" version="1.1" encoding="UTF-8" />
  <xsl:template match="/">
    <out>&#x82;&#x8F;</out>
  </xsl:template>
</xsl:stylesheet>
