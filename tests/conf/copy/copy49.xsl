<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  <!-- FileName: copy49 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.5 -->
  <!-- Creator: Tony Pentinnen (submitted by Tom Amiro)  -->
  <!-- Purpose: Verify xml:lang attributes can be copied from source to result -->

<xsl:template match="doc">
<document>
<xsl:copy-of select="@*" />
</document>
</xsl:template>
</xsl:stylesheet>
