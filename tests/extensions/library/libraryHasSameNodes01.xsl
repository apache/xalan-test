<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">
<xsl:output indent="yes"/>

  <!-- FileName: libraryHasSameNodes01.xsl -->
  <!-- Creator: Shane Curcuru -->
  <!-- Purpose: Basic test of hasSameNodes(ns, ns) extension function -->

<xsl:template match="doc">
  <out>
    <test desc="selects true (abc, abc)">
      <xsl:copy-of select="xalan:hasSameNodes(list[@name='abc']/item, list[@name='abc']/item)"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="xalan:hasSameNodes(list[@name='abc']/item, list[@name='abc']/item)"/>
    </test>
    <test desc="selects true (abc, abc and parent)">
      <xsl:copy-of select="xalan:hasSameNodes(list[@name='abc'], list[@name='abc'])"/>
    </test>
    <test desc="selects false (abc, xyz)">
      <xsl:copy-of select="xalan:hasSameNodes(list[@name='abc']/item, list[@name='xyz']/item)"/>
    </test>
    <test desc="selects false (abc, abc2)">
      <xsl:copy-of select="xalan:hasSameNodes(list[@name='abc']/item, list[@name='abc2']/item)"/>
    </test>
    <test desc="selects false (abc, abc[1])">
      <xsl:copy-of select="xalan:hasSameNodes(list[@name='abc']/item, list[@name='abc']/item[1])"/>
    </test>
    <test desc="selects false (abc[1], abc)">
      <xsl:copy-of select="xalan:hasSameNodes(list[@name='abc']/item[1], list[@name='abc']/item)"/>
    </test>
    <test desc="selects true (abc[1], abc[1])">
      <xsl:copy-of select="xalan:hasSameNodes(list[@name='abc']/item[1], list[@name='abc']/item[1])"/>
    </test>
  </out>
</xsl:template>

</xsl:stylesheet>