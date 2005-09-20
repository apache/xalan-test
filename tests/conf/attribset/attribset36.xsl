<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: attribset36 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.3 Creating Attributes -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Verify adding an attribute to an element after a comment has
       been added to it is an error. The attributes can be ignored.
       The spec doesn't explicitly say this is disallowed, as it does
       for child elements, but it makes sense to have the same treatment. -->

<xsl:template match="doc">
  <Out>
    <xsl:element name="Element1">
      <xsl:attribute name="Att1">OK</xsl:attribute>
      <xsl:comment>Should not break</xsl:comment>
      <xsl:attribute name="AttX">Wrong</xsl:attribute>
      <xsl:element name="Element2"/>
    </xsl:element>
  </Out>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
