<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: STRerr11 -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 4.2 String Functions  -->
  <!-- Purpose: Test of 'substring-after()' with too many arguments -->
  <!-- ExpectedException: substring-after() has too many arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="substring-after('ENCYCLOPEDIA','LOPE',doc)"/>
  </out>
</xsl:template>

</xsl:stylesheet>
