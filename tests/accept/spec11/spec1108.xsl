<?xml version="1.1"?>

  <!-- FileName: spec1108.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                When NEL (0x0085) and LSEP (0x2028) appear as Numeric 
                Character Reference (NCR), they must not be treated as whitespaces.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     version="1.0">

  <xsl:output method="xml" version="1.1" />
  <xsl:strip-space elements="Country" />

  <xsl:template match="@*|node()">
   <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
   </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
