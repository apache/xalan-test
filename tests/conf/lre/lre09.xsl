<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:ped="http://www.tester.com">

<xsl:output indent="yes"/>

  <!-- FileName: lre09 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements with xsl:element. -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: It is an error if the string that results from instantiating 
       the attribute value template is not a QName. An XSLT processor may signal 
       the error; if it does not signal the error, then it must recover by making 
       the the result of instantiating the xsl:element element be the sequence of 
       nodes created by instantiating the content of the xsl:element element, 
       excluding any initial attribute nodes. -->

<xsl:template match="doc">
 <root>
  <xsl:element name="{a}"/>
  <xsl:element name="{b}">
    <xsl:attribute name="attr1">Hello</xsl:attribute>
	<xsl:attribute name="attr2">Goodbye</xsl:attribute>
	<HTML><body>
		<xsl:element name="text">
		  <xsl:attribute name="font">Courier</xsl:attribute>
		  What's Up
		</xsl:element>
	</body></HTML>
  </xsl:element>
 </root>
</xsl:template>

</xsl:stylesheet>
