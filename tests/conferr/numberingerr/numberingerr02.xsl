<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- CaseName: numberingerr02 -->
  <!-- Author: David Marston -->
  <!-- Purpose: Test of bad value for level attribute. -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(number)/ulist[1]/item[1]/p[1]/text()[1]" -->
  <!-- Scenario: operation="message" StandardError="Bad value on level attribute: sides" -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="note">
  <xsl:number level="sides" from="chapter" format="(1) "/>
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
