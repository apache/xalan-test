<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: EXTENDerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 14 Extensions -->
  <!-- Purpose: Test function-available with too many arguments. -->
  <!-- ExpectedException: function-available requires one argument -->
  <!-- ExpectedException: FuncExtFunctionAvailable only allows 1 arguments -->

<xsl:template match="/">
  <out>
    <xsl:value-of select="function-available('document','whatever')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
