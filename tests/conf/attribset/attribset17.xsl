<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

  <!-- FileName: attribset17 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Verify that 'checked' attribute of HTML element input is correctly set. -->

<xsl:template match="ticket/input">
<HTML>
  <Form>
	<xsl:value-of select="zoneone"/> 
	<Input Type="checkbox">
	  <xsl:if test="zoneone/@checked='1'">
	    <xsl:attribute name="CHECKED">CHECKED</xsl:attribute>
      </xsl:if>
    </Input>
  </Form>
</HTML>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
