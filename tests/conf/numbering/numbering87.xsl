<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:z="z.test.com">

  <!-- CaseName: numbering87 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Number the namespaced elements when mixed with non-namespaced ones. -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(number)/ulist[2]/item[1]/p[1]/text()[5]" -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(number)/ulist[2]/item[3]/p[1]/text()[6]" -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(number)/ulist[1]/item[2]/p[1]/text()[3]" -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(number)/ulist[1]/item[3]/p[1]/text()[1]" -->
  <!-- SpecCitation: Rec="XSLT" Version="1.0" type="OASISptr1" place="id(convert)/p[2]/text()[3]" -->
  <!-- Scenario: operation="standard-XML" -->

<xsl:template match="z:doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="z:note">
  <xsl:element name="znote">
    <xsl:attribute name="number">
      <xsl:number level="any" from="z:doc"/>
    </xsl:attribute>
    <xsl:value-of select="."/>
  </xsl:element>
</xsl:template>

<xsl:template match="note">
  <xsl:element name="chapter-note">
    <xsl:attribute name="number">
      <xsl:number level="single" from="z:chapter"/>
    </xsl:attribute>
    <xsl:value-of select="."/>
  </xsl:element>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
