<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
<xsl:output method="xml"/>

  <!-- FileName:  -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 -->
  <!-- Purpose: Test has no exceptions. -->

<xsl:template match="/">
<root>
<data1 attr1="hello" attr2="goodbye"/>
<data2 attr1="goodbye" attr2="hello"/>
This is a test
</root>
</xsl:template>

</xsl:stylesheet>