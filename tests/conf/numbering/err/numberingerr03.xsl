<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- CaseName: numberingerr03 -->
  <!-- Author: David Marston -->
  <!-- Purpose: Test of bad specification for letter-value. -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(convert)/p[5]/text()[1]" -->
  <!-- Scenario: operation="message" StandardError="Bad value on letter-value attribute" -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="note">
  <xsl:number level="any" from="chapter" format="&#x03b1;" letter-value="invalid"/>
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
