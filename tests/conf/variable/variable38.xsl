<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: variable38 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 11.3 Using Variables and Parameters with xsl:copy-of -->
  <!-- Purpose: Test for using xsl:copy-of with variable set to a number. -->
  <!-- This should have the same result as xsl:value-of, stringified number. -->
  <!-- Author: David Marston -->

<xsl:template match="doc">
  <xsl:variable name="x" select="10+7"/><!-- Use of + causes it to be numeric -->
  <out>
    <xsl:copy-of select="$x"/>
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
