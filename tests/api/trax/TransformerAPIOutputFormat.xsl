<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- Include all XSLT spec xsl:output attrs -->
<xsl:output method="xml"
            version="123.45"
            encoding="UTF-16"
            standalone="yes"
            doctype-public="this-is-doctype-public"
            doctype-system="this-is-doctype-system"
            cdata-section-elements="cdataHere"
            indent="yes"
            media-type="text/test/xml"
            omit-xml-declaration="yes" />

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
