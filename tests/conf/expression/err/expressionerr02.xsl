<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: EXPRerr02 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.4 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Invoke unparsed-entity-uri function with too many arguments -->
  <!-- ExpectedException: The unparsed-entity-uri function should take one argument -->
  <!-- ExpectedException: FuncUnparsedEntityURI only allows 1 arguments -->

<xsl:template match="doc">
  <out>
    <xsl:value-of select="unparsed-entity-uri('foo','hatch-pic')"/>
  </out>
</xsl:template>

</xsl:stylesheet>
