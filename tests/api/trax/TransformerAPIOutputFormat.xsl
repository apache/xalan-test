<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml"
            omit-xml-declaration="yes" 
            cdata-section-elements="cdataHere" />

  <xsl:template match="doc">
    <out>
      <cdataHere>CDATA? or not?</cdataHere>
      <xsl:text>foo</xsl:text>
      <xsl:copy-of select="cdataHere" />
      <out2>
        <xsl:text>bar</xsl:text>
        <xsl:copy-of select="selector" />
      </out2>
    </out>
  </xsl:template>
</xsl:stylesheet>
