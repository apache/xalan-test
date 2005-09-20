<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: whitespace12 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 16 -->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: Test xsl:output with indent. -->

<xsl:output indent="yes"/>

<xsl:strip-space elements="document"/>

<xsl:template match="document">
  <out>
    <xsl:apply-templates/>
  </out>
</xsl:template>

<xsl:template match="section">
  <xsl:text>Position:</xsl:text><xsl:value-of select="position()"/>
  <xsl:text>,Last:</xsl:text><xsl:value-of select="last()"/><xsl:text>
</xsl:text>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
