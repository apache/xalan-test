<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">
<xsl:output indent="yes"/>

  <!-- FileName: libraryDifference01.xsl -->
  <!-- Creator: Shane Curcuru -->
  <!-- Purpose: Basic test of difference(ns, ns) extension function -->

<xsl:template match="doc">
  <out>
    <test desc="selects abc (abc, xyz)">
      <xsl:copy-of select="xalan:difference(list[@name='abc']/item, list[@name='xyz']/item)"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:difference(list[@name='abc']/item, list[@name='xyz']/item)"/>
    </test>
    <test desc="selects xyz (xyz, abc)">
      <xsl:copy-of select="xalan:difference(list[@name='xyz']/item, list[@name='abc']/item)"/>
    </test>
    <test desc="selects nothing (abc, abc)">
      <xsl:copy-of select="xalan:difference(list[@name='abc']/item, list[@name='abc']/item)"/>
    </test>
    <test desc="selects nothing (abc, abc and parent)">
      <xsl:copy-of select="xalan:difference(list[@name='abc'], list[@name='abc'])"/>
    </test>
    <test desc="selects bc (abc, abc[1])">
      <xsl:copy-of select="xalan:difference(list[@name='abc']/item, list[@name='abc']/item[1])"/>
    </test>
    <test desc="selects nothing (abc[1], abc)">
      <xsl:copy-of select="xalan:difference(list[@name='abc']/item[1], list[@name='abc']/item)"/>
    </test>
    <test desc="selects a (abc[1], abc[2])">
      <xsl:copy-of select="xalan:difference(list[@name='abc']/item[1], list[@name='abc']/item[2])"/>
    </test>
  </out>
</xsl:template>

</xsl:stylesheet>