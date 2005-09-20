<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> 

  <!-- FileName: x45import -->
  <!-- Purpose: Imported by NumberFormat45 -->

<xsl:decimal-format decimal-separator=',' grouping-separator='.'/>

<xsl:template name="sub">
  <sub>
    <xsl:value-of select="format-number(12345.67,'000.000,###')"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="format-number(-98765.4321,'##0.000,000')"/>
  </sub>
</xsl:template>


  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
