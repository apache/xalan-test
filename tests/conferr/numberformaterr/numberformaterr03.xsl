<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: NUMBERFORMATerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Attempt to put a child on decimal-format. -->
  <!-- ExpectedException: xsl:text not allowed inside xsl:decimal-format -->
  <!-- ExpectedException: xsl:text is not allowed in this position in the stylesheet! -->

<xsl:decimal-format NaN="non-numeric">
  <xsl:text>This should not appear!</xsl:text>
</xsl:decimal-format>

<xsl:template match="doc">
  <out>
    <xsl:value-of select="format-number('foo','#############')"/>
  </out>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
