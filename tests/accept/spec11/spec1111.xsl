<?xml version="1.1"?>
  <!-- FileName: spec1111.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                An IRI can be passed to a document function. 
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" version="1.1" />
  <xsl:template match="/">
      <xsl:copy-of select="document('spec1111ab%C3%A7d%C3%A9')"/>
  </xsl:template>
</xsl:stylesheet>
