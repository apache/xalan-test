<?xml version="1.1"?>

  <!-- FileName: spec1104.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                When NEL (0x0085) and LSEP (0x2028) appear as Numeric 
                Character Reference (NCR), they must be output as NCR.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" version="1.1" encoding="UTF-8" />
  <xsl:template match="/">
    <out>&#x85;&#x2028;</out>
  </xsl:template>
</xsl:stylesheet>
