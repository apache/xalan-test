<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: outperr13 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.3 Creating Processing Instructions. -->
  <!-- Purpose: Processing instruction content can't create nodes other then 
       text nodes. -->
  <!-- ExpectedException: Can not add xsl:element to xsl:processing-instruction -->
  <!-- ExpectedException: xsl:element is not allowed in this position in the stylesheet! -->

<xsl:template match="doc/tag">
 <out>
   <xsl:processing-instruction name="mytag">
	  <xsl:element name="trythis">
		<xsl:value-of select="."/>
	  </xsl:element>
   </xsl:processing-instruction>
 </out>
</xsl:template>
 
</xsl:stylesheet>