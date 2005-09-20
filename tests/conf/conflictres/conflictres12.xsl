<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: conflictres12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 5.5 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Test that child and descendant are equal in priority. -->
  <!-- should see 2 conflict warnings -->

<xsl:template match="doc">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="a//b">
  <xsl:text>Descendant,</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="a/b">
  <xsl:text>Child,</xsl:text>
  <xsl:apply-templates/>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
