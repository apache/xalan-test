<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:ped="http://www.ped.com">
<xsl:output indent="yes"/>

  <!-- FileName: lre10 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements with xsl:element. -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: If the namespace attribute is present, then it also is interpreted as 
       an attribute value template... -->

<xsl:template match="/">
 <root>
   <xsl:element name="{docs/a}" namespace="{docs/a/@href}"/>
   <xsl:element name="{docs/b}" namespace="{docs/b/@href}"/>
   <xsl:element name="{docs/c}" namespace="{docs/c/@href}"/>
 </root>
</xsl:template>

</xsl:stylesheet>
