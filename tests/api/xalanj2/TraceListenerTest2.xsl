<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/">
    <doc>
      <mode-none>
        <xsl:apply-templates select="list" />
      </mode-none>
    </doc>
  </xsl:template>

  <xsl:template match="item">
    <pie>
      <xsl:copy/>
    </pie>
  </xsl:template>

  <xsl:template match="list">
    <out-list>
      <xsl:for-each select="item">
        <out-item>
          <xsl:value-of select="."/>
        </out-item>
      </xsl:for-each>
    <xsl:apply-templates select="list" />
    </out-list>
  </xsl:template>
     
</xsl:stylesheet>
