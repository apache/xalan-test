<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.2 -->
  <!-- Purpose: Test for two name attributes in xsl:key. -->
  <!-- Creator: David Marston -->
  <!-- ExpectedException: Attribute "name" was already specified for element "xsl:key". -->
  <!-- ExpectedException: org.apache.xalan.xslt.XSLProcessorException: Attribute "name" was already specified for element "xsl:key". -->

<xsl:output indent="yes"/>

<xsl:key name="mykey" match="div" use="title" name="otherkey" />

<xsl:template match="doc">
  <xsl:value-of select="key('mykey', 'Introduction')/p"/>
  <xsl:value-of select="key('mykey', 'Stylesheet Structure')/p"/>
  <xsl:value-of select="key('mykey', 'Expressions')/p"/>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
