<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey33 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Use key() for sorting in for-each. -->

<xsl:key name="MonthNum" match="monthtab/entry/number" use="../name" />

<xsl:template match="doc">
  <out>
    <xsl:text>Birthdays as found...
</xsl:text>
    <xsl:for-each select="birthday">
      <xsl:value-of select="@person"/><xsl:text>: </xsl:text>
      <xsl:value-of select="month"/><xsl:text> </xsl:text>
      <xsl:value-of select="day"/><xsl:text>
</xsl:text>
    </xsl:for-each>
    <xsl:text>
Birthdays in chronological order...
</xsl:text>
    <xsl:for-each select="birthday">
      <xsl:sort select="key('MonthNum',month)" data-type="number" />
      <xsl:sort select="day" data-type="number" />
      <xsl:value-of select="@person"/><xsl:text>: </xsl:text>
      <xsl:value-of select="month"/><xsl:text> </xsl:text>
      <xsl:value-of select="day"/><xsl:text>
</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
