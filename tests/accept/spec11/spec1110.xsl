<?xml version="1.1"?>
  <!-- FileName: spec1110.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                namespace can be an IRI. 
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" version="1.1" indent="yes" />
  <xsl:template match="/">
    <out xmlns:pre="http://example.org/pre&#xA2;">
      <xsl:copy-of select="indoc/ch1/ch2"/>
    </out>
  </xsl:template>
</xsl:stylesheet>
