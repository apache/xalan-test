<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: impinclerr10 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.6 Overriding Template Rules -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Try to put a child element on apply-imports -->
  <!-- ExpectedException: xsl:with-param is not allowed in this position in the stylesheet! -->

<xsl:import href="../fragments/impwparam.xsl"/>

<xsl:output method="xml" encoding="UTF-8" indent="no"/>

<xsl:template match="doc">
  <out>
    <xsl:apply-templates select="*"/>
  </out>
</xsl:template>

<xsl:template match="tag">
  <div style="border: solid blue">
    <xsl:apply-imports>
      <xsl:with-param name="p1" select="'main'"/>
    </xsl:apply-imports>
  </div>
</xsl:template>

</xsl:stylesheet>