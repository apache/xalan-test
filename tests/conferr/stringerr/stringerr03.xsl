<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: stringerr03 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'normalize-space()' function with too many arguments -->
  <!-- ExpectedException: normalize-space() has too many arguments -->
  <!-- ExpectedException: FuncNormalizeSpace only allows 0 or 1 arguments -->

<xsl:template match="/doc">
  <out>
    <xsl:value-of select="normalize-space(a,'&#9;&#10;&#13; ab    cd  ')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
