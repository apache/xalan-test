<?xml version="1.1"?>
  <!-- FileName: spec1112.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                namespace attribute of <xsl:element> element can be an IRI. 
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="1.0">
  <xsl:output method="xml" version="1.1" indent="yes"/>

  <xsl:template match="/">
    <xsl:element name="pre:out"  
                 namespace="http://example.org/pre&#xC0;">
    </xsl:element>
</xsl:template>
</xsl:stylesheet>
