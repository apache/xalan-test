<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: POSerr03 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.1 Node Set Functions -->
  <!-- Purpose: Test too few arguments to count(). -->
  <!-- ExpectedException: The count function should take one argument -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="count()"/>
  </out>
</xsl:template>

</xsl:stylesheet>
