<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey51 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.1 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test two calls to generate-id() on the same file. All IDs should be
    the same. If both filenames were given literally, the spec says that IDs must match.
    Retrieving the name from the principal XML document should still clearly mean the
    same file. Putting nodes in a variable is more of a gray area. -->

<xsl:template match="doc">
  <out>
    <xsl:variable name="nodes" select="document('idkey49b.xml')//body" />
    <xsl:text>&#10;</xsl:text>
    <from-a>
      <xsl:for-each select="document(a)//body">
        <xsl:value-of select="generate-id(.)"/><xsl:text>,  </xsl:text>
      </xsl:for-each>
    </from-a>
    <xsl:text>&#10;</xsl:text>
    <from-v>
      <xsl:for-each select="$nodes">
        <xsl:value-of select="generate-id(.)"/><xsl:text>,  </xsl:text>
      </xsl:for-each>
    </from-v>
  </out>
</xsl:template>

</xsl:stylesheet>