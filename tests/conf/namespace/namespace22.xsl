<?xml version="1.0"?>
<xsl:stylesheet	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
      xmlns:lotus="http://www.lotus.com"
      xmlns="http://www.w3.org/TR/REC-html40">

  <!-- FileName: namespace22-->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1 Creating Elements (Namespace Node Inhertiance)  -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Determine how namespaces are inherited down to succeeding elements.
       Currently this is not an atomic test. This needs more investagation. -->

<xsl:template match = "doc">
 <root>
    <xsl:text>&#010;</xsl:text>
 	<Out1/>
 	<xsl:element name="Element1" namespace="http://www.element1.com"/><xsl:text>&#010;</xsl:text>
 	<Out2/>
 	<xsl:element name="Element2"/><xsl:text>&#010;</xsl:text>
 	<Out3/>
 	<xsl:element name="Element3"/>
 </root>
</xsl:template>

</xsl:stylesheet>
