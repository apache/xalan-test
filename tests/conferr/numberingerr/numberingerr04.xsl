<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- CaseName: numberingerr04 -->
  <!-- Author: David Marston -->
  <!-- Purpose: Test invalid path specified in from. -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(number)/ulist[1]/item[3]/p[1]/text()[1]" -->
  <!-- Scenario: operation="message" StandardError="from attribute must be a node-set" -->
  <!-- ExpectedException: Unknown nodetype: true -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="note">
  <xsl:number level="single" from="true()" format="(1) "/>
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
