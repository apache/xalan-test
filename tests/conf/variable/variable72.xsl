<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:ex="http://xml.apache.org/xalan"
                extension-element-prefixes="ex">

  <!-- FileName: variable72 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Ensure that traversal of local RTF gets the right one. -->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:template match="doc">
  <!-- Define a couple variables -->
  <xsl:variable name="var1">
    <t0>var1-begin
      <t1>var1-first1</t1>
      <t2>var1-first2</t2>
      <t1>var1-second1</t1>
    </t0>
  </xsl:variable>
  <xsl:variable name="var2">
    <t0>var2-begin
      <t1>var2-first1</t1>
      <t2>var2-first2</t2>
      <t1>var2-second1</t1>
    </t0>
  </xsl:variable>
  <out>
    <!-- First, force evaluation of each variable -->
    <junk>
      <xsl:text>$var1 summary: </xsl:text>
      <xsl:value-of select="$var1"/>
      <xsl:text>
</xsl:text>
      <xsl:text>$var2 summary: </xsl:text>
      <xsl:value-of select="$var2"/>
    </junk>
    <xsl:text>
</xsl:text>
    <xsl:text>The t1 elements in $var2 are </xsl:text>
    <xsl:for-each select="ex:nodeset($var2)//t2/preceding::t1">
      <xsl:value-of select="."/>
      <xsl:text>,</xsl:text>
    </xsl:for-each>
  </out>
</xsl:template>

</xsl:stylesheet>
