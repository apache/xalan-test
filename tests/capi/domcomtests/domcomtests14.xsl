<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:cnn="http://www.bad.com"
				exclude-result-prefixes="cnn">
	
<xsl:output method="xml"/>

  <!-- FileName:  -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 -->
  <!-- Purpose: Test has no exceptions. -->

<xsl:template match="/">
<root>
<data1 attr1="hello" attr2="goodbye">
<data2 attr1="goodbye" attr2="hello">
<data3 attr1="What in the world">DANIEL</data3></data2>
<cnn:data4 attr1="olleh" cnn:attr2="eybdoog">DICK</cnn:data4>
</data1>
This is a test
</root>
</xsl:template>

</xsl:stylesheet>
