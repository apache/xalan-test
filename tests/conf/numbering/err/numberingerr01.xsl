<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- CaseName: numberingerr01 -->
  <!-- Author: David Marston -->
  <!-- Purpose: Test of excessive size of grouping-separator. -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(convert)/p[6]/text()[1]" -->
  <!-- Scenario: operation="message" StandardError="grouping-separator must be one character long." -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="note">
  <xsl:number level="any" from="chapter" format="(1) " grouping-size="2" grouping-separator="toobig" />
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
