<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- CaseName: numberingerr07 -->
  <!-- Author: David Marston -->
  <!-- Purpose: Put xsl:number at top level, which is illegal. -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(stylesheet-element)/p[3]/text()[1]" -->
  <!-- Scenario: operation="message" StandardError="xsl:number not allowed inside a stylesheet" -->

<xsl:number count="item" from="ol"/>

<xsl:template match="doc">
  <out>This should fail</out>
</xsl:template>

</xsl:stylesheet>
