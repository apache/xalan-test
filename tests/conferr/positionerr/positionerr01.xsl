<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: positionerr01 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.1 Node Set Functions -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Last should not have any arguments. -->
  <!-- ExpectedException: zero arguments expected  -->
  <!-- ExpectedException: FuncLast only allows 0 arguments -->

<xsl:template match="doc">
  <out>
    Last is h: <xsl:value-of select="*[last(*,2)]"/>
  </out>
</xsl:template>

</xsl:stylesheet>
