<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- CaseName: numberingerr06 -->
  <!-- Author: David Marston -->
  <!-- Purpose: Test of attempt to express negative numbers in Roman numerals. -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(convert)/ulist[1]/item[5]/p[1]/text()[1]" -->
  <!-- Scenario: operation="message" StandardError="I and i can only format positive numbers" -->
  <!-- Discretionary: number-not-positive="raise-error" -->
  <!-- Gray-area: number-NaN-handling="raise-error" -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="chapter">
  <xsl:for-each select="note">
    <xsl:number format="(I) " value="position() -10" />
    <xsl:value-of select="."/><xsl:text>&#10;</xsl:text>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
