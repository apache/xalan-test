<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml"/>

<!-- FileName: ErrorListenerTest -->
<!-- Creator: shane_curcuru@lotus.com -->
<!-- Purpose: Various tests that ErrorListeners get called for illegal stylesheets. -->
<!-- ExpectedMessage: TBD -->
<!-- ExpectedError: TBD -->
<!-- ExpectedFatalError: xsl:decimal-format names must be unique. Name "myminus" has been duplicated -->

<!-- duplicating decimal-format names is illegal, but shouldn't 
     affect other processing in the stylesheet, so it should be 
     recoverable, allowing processing to continue.
--> 
<xsl:decimal-format name="myminus" minus-sign='_' />
<xsl:decimal-format name="myminus" minus-sign='`' />

<xsl:template match="list">
  <list-out>
    <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
    <xsl:message terminate="no">ExpectedMessage from:<xsl:value-of select="@name"/></xsl:message>
    <xsl:apply-templates/>
  </list-out>
</xsl:template>

<xsl:template match="item">
  <item-out>
    <xsl:value-of select="."/>
  </item-out>
</xsl:template>

</xsl:stylesheet>