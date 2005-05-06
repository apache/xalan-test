<?xml version="1.1"?>

  <!-- FileName: spec1105.xsl -->
  <!-- Purpose: To output a document with method 'xml' and version '1.1'. 
                When NEL (0x0085) and LSEP (0x2028) appear as Numeric 
                Character Reference (NCR) within a Comment or a Processing
                Instruction, they must be output as actual value.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" version="1.1" encoding="UTF-8" 
              indent="yes"/>
  <xsl:template match="/">
    <out/>
    <xsl:comment> This is a comment &#x2028; </xsl:comment>
    <xsl:processing-instruction name="hellopi"> hello &#x2028;   &#x85;   </xsl:processing-instruction>
  </xsl:template>
</xsl:stylesheet>
