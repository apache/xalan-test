<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: stringerr10 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test of 'substring-after()' with one argument -->
  <!-- ExpectedException: substring-after() requires two arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="substring-after('ENCYCLOPEDIA')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
