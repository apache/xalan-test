<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variableerr17 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Try to set top-level params with circular references -->
  <!-- ExpectedException: Variable defined using itself -->
  <!-- Author: David Marston -->

<xsl:variable name="circle0" select="concat('help',$circle1)"/>
<xsl:param name="circle1" select="concat('help',$circle0)"/>

<xsl:template match="doc">
   <out>
      <xsl:value-of select="$circle0"/>
   </out>
</xsl:template>

</xsl:stylesheet>