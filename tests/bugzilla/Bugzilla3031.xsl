<?xml version="1.0"?>
<xsl:transform version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <first worksCorrectly="YES">
      <xsl:for-each select="/foo/bar">
        <xsl:for-each select="/foo/bang[bat=current()]">
          <xsl:copy-of select="."/>
        </xsl:for-each>
      </xsl:for-each>
    </first>
    <second worksCorrectly="YES! (should do the same thing)">
      <xsl:for-each select="/foo/bar">
        <xsl:for-each select="/foo/bang[bat[.=current()]]">
          <xsl:copy-of select="."/>
        </xsl:for-each>
      </xsl:for-each>
    </second>
  </xsl:template>

</xsl:transform>
