<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output cdata-section-elements="test"/>
<xsl:output method="html" 
            doctype-public="-//W3C//DTD HTML 4.0 Transitional"
			cdata-section-elements="example"/>


  <!-- FileName: outp46 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 16 Output -->
  <!-- Purpose: All xsl:output elements are merged into a single element.
       cdata-section-elements will contain the union of the specified values.
       It is an error if there is more than one such value for an attribute.
       The processor may signal an error; or recover by using the value
       that occurs last in the stylesheet.
       
       Both example and test should be wrapped by CDATA, and the output should
       be HTML. -->

<xsl:template match="/">
	<example>&lt;foo></example>
	<test>]]&gt;</test>
</xsl:template>
 
</xsl:stylesheet>
