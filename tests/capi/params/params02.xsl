<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableman02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters -->
  <!-- Purpose:  Test setting several parameters externally. Run from
       special bat file which contains the following additional options
       -param in1 'A' -param in2 'B' -param in3 'C' etc.
       Suggest setting 1-5, so you see default on 6. -->
  <!-- Author: David Marston -->

<xsl:param name="in1" select="'default1'"/>
<xsl:param name="in2" select="'default2'"/>
<xsl:param name="in3" select="'default3'"/>
<xsl:param name="in4" select="'default4'"/>
<xsl:param name="in5" select="'default5'"/>
<xsl:param name="in6" select="'default6'"/>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="$in1"/>
    <xsl:value-of select="$in2"/>
    <xsl:value-of select="$in3"/>
    <xsl:value-of select="$in4"/>
    <xsl:value-of select="$in5"/>
    <xsl:value-of select="$in6"/>
  </out>
</xsl:template>

</xsl:stylesheet>