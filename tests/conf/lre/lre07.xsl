<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:ped="http://www.test.com">
<xsl:output indent="yes"/>

  <!-- FileName: lre07 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.2 Creating Elements with xsl:element. -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: The xsl:element element allows an element to be created with a 
       computed name. The expanded-name of the element to be created is specified 
       by a required name attribute and an optional namespace attribute. -->

<xsl:template match="doc">
 <root>
  <xsl:element name="out1"/>
  <xsl:element name="out2" namespace="http://www.test.com"/>
  <xsl:element name="out3" namespace="http://www.bdd.com"/>
 </root>
</xsl:template>

</xsl:stylesheet>
