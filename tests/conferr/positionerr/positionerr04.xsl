<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: positionerr04 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.1 Node Set Functions -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test too many arguments to count(). -->
  <!-- ExpectedException: The count function should take one argument -->
  <!-- ExpectedException: FuncCount only allows 1 arguments -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="count(*,4)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
