<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
				xmlns:cnn="http://www.cnn.com">

<xsl:output method="xml"/>

  <!-- FileName:  -->
  <!-- Document: http://www.w3.org/TR/xpath -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 2.2 -->
  <!-- Purpose: Test has no exceptions. -->

<xsl:template match="/">
<root>
<cnn:data attr1="What in the world">DANIEL</cnn:data>
<espn:data xmlns:espn="http://www.espn.com" espn2:attr1="hello" xmlns:espn2="http://www.espn2.com">ESPN and ESPN2</espn:data>
<data2 attr1="olleh" xyz:attr2="eybdoog" xmlns:xyz="http://www.xyz.com">DICK</data2>
This is a test
</root>
</xsl:template>

</xsl:stylesheet>
