<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:math="http://exslt.org/math"
    exclude-result-prefixes="math">
<xsl:output method="xml" encoding="UTF-8" indent="yes" />

  <!-- FileName: libraryMath01.xsl -->
  <!-- Creator: Morris Kwan -->
  <!-- Purpose: Test of the math:min() and math:max() extension functions -->

<xsl:template match="/">
  <out>
    <test desc="selects -3">
      <xsl:value-of select="math:min(/doc/num)"/>
    </test>
    <test desc="selects NaN">
      <xsl:value-of select="math:min(/doc/abc)"/>
    </test>
    <test desc="selects NaN">
      <xsl:value-of select="math:min(/doc/str)"/>
    </test>
    <test desc="selects 5">
      <xsl:value-of select="math:max(/doc/num)"/>
    </test>
    <test desc="selects NaN">
      <xsl:value-of select="math:max(/doc/abc)"/>
    </test>
    <test desc="selects NaN">
      <xsl:value-of select="math:max(/doc/str)"/>
    </test>
  </out>
</xsl:template>

</xsl:stylesheet>