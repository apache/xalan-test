<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribseterr01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes -->
  <!-- Purpose: Verify creating nodes other than text nodes during instantiation
       of the content of the xsl:attribute element is an error. Offending nodes
       can be ignored. -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: ElemTemplateElement error: Can not add foo to xsl:attribute -->
  <!-- ExpectedException: ElemTemplateElement error: Can not add foo to xsl:attribute -->
  <!-- ExpectedException: foo is not allowed in this position in the stylesheet! -->

<xsl:template match="/">
   <Out>
      <xsl:attribute name="attr1">
		 <foo/>
         <xsl:processing-instruction name="PDPI">Process this</xsl:processing-instruction>
		 <xsl:comment>This should be ignored</xsl:comment>
		 <xsl:copy-of select="doc2"/>
		OK
      </xsl:attribute>
   </Out>
</xsl:template>

</xsl:stylesheet>