<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: processorinfoerr06 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test bad argument to system-property, QName's prefix undeclared -->
  <!-- ExpectedException: property not supported -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="system-property('lotus:version')"/>
  </out>
</xsl:template>
 
</xsl:stylesheet>
