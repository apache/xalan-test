<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/">
    <doc>
      <mode-none>
        <xsl:apply-templates select="item" /><!-- ElemTemplateElement[xsl:apply-templates;L7;C46;select=itemtest=item; -->
      </mode-none>
      <mode-ala>
        <xsl:apply-templates select="list" mode="ala" /><!-- selected:ElemTemplateElement[xsl:apply-templates;L10;C57;select=list -->
      </mode-ala>
    </doc>
  </xsl:template>

  <xsl:template match="item">
    <pie>
      <xsl:copy/>
    </pie>
  </xsl:template>

  <xsl:template match="list" mode="ala">
    <icecream>text-literal-chars<xsl:text>xsl-text-content</xsl:text><xsl:copy-of select="."/><!-- ElemTemplateElement[xsl:copy-of;L22;C96;select=.select=.; -->
    </icecream>
  </xsl:template>
     
</xsl:stylesheet>
