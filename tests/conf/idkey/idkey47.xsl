<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkey47 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.2 -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test complex key()//x/x in match pattern. -->


<xsl:key name="Info" match="Level1 | Level2 | Level3" use="@ID"/>

<xsl:template match="/">
 <out>
	<xsl:apply-templates select="//Level3"/>
 </out>
</xsl:template>

<xsl:template match="key('Info','id1')//Level3/Level3">
	<xsl:text>&#10;</xsl:text>
	<xsl:element name="Simple-Match">
		<xsl:value-of select="Name/@First"/>
	</xsl:element>
</xsl:template>


<xsl:template match="*">
	<xsl:text>&#10;</xsl:text>
	<xsl:element name="No-Match">
		<xsl:value-of select="Name/@First"/>
	</xsl:element>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
