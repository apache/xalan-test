<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: PROPerr05 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 -->
  <!-- Purpose: Test too many arguments to system-property -->
  <!-- ExpectedException: system-property requires one argument -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="system-property('xsl:version','xsl:vendor')"/>
  </out>
</xsl:template>
 
</xsl:stylesheet>
