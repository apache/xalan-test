<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- FileName: OutputSettingsXML.xsl -->
  <!-- Purpose: Legal XML output for use with OutputSettingsTest.java -->

<!-- Include various XSLT spec xsl:output attrs -->
<xsl:output method="xml"
            doctype-public="this-is-doctype-public"
            doctype-system="this-is-doctype-system"
            cdata-section-elements="cdataHere"
            indent="yes" />

<xsl:template match="/">
  <out>
  <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="html-tag">
    <header>
      <xsl:element name="title"><xsl:value-of select="head-tag/title-tag/@text"/></xsl:element>
      <xsl:text>xsl:text within head tag</xsl:text>
    </header>
    <document>
      <xsl:apply-templates select="body-tag"/>
      <xsl:text disable-output-escaping="yes">&lt;p>fake 'p' element&lt;/p></xsl:text>
      <!-- all xml elements below, just for fun -->
      <entities>&#034; &#038; &#060; &#062;</entities>
    </document>
</xsl:template>

<xsl:template match="body-tag">
    <xsl:apply-templates select="p-tag | ul-tag"/>
</xsl:template>

<xsl:template match="p-tag">
  <xsl:element name="p">
    <xsl:value-of select="."/>
  </xsl:element>
</xsl:template>
 
<xsl:template match="ul-tag">
  <ul>
    <xsl:copy-of select="."/>
  </ul>
</xsl:template>
</xsl:stylesheet>
