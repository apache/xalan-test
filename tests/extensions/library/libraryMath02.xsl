<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:math="http://exslt.org/math"
    exclude-result-prefixes="math">
<xsl:output method="xml" encoding="UTF-8" indent="yes" />

  <!-- FileName: libraryMath02.xsl -->
  <!-- Creator: Morris Kwan -->
  <!-- Purpose: Test of the math:highest() and math:lowest() extension functions -->

<xsl:template match="/">
  <out>
    <test desc="selects 5, 5">
      <xsl:copy-of select="math:highest(/doc/num)"/>
    </test>
    <test desc="selects nothing">
      <xsl:copy-of select="math:highest(/doc/abc)"/>
    </test>
    <test desc="selects nothing">
      <xsl:copy-of select="math:highest(/doc/str)"/>
    </test>
    <test desc="selects -3">
      <xsl:copy-of select="math:lowest(/doc/num)"/>
    </test>
    <test desc="selects nothing">
      <xsl:copy-of select="math:lowest(/doc/abc)"/>
    </test>
    <test desc="selects nothing">
      <xsl:copy-of select="math:lowest(/doc/str)"/>
    </test>
  </out>
</xsl:template>

</xsl:stylesheet>