<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes -->
  <!-- Purpose: Verify adding an attribute to node that is not an element
  	   is an error.  The attributes can be ignored.-->
  <!-- ExpectedException: Can not add xsl:attribute to xsl:attribute -->
  <!-- ExpectedException: ElemTemplateElement error: Can not add xsl:attribute to xsl:attribute -->

<xsl:template match="/">
  <out>
    <xsl:element name="Element1">
      <xsl:attribute name="Att1">
        <xsl:attribute name="Att2">Wrong</xsl:attribute>
          OK
        </xsl:attribute>
      </xsl:element>	  
    <xsl:attribute name="Att1">Also-Wrong</xsl:attribute>
  </out>
</xsl:template>

</xsl:stylesheet>