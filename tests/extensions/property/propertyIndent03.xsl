<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt"
    exclude-result-prefixes="xalan">

  <!-- FileName: propertyIndent03 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test xalan:indent-amount set to 10 -->

<xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="10"/>

<xsl:template match="doc">
  <out>
    <xsl:element name="a">
      <xsl:element name="b">
        <xsl:element name="c">
          <xsl:element name="d">Okay</xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </out>
</xsl:template>

</xsl:stylesheet>
