<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
    xmlns:foo="http://foo.com"
    xmlns:baz="http://foo.com"
    exclude-result-prefixes="foo baz">

  <!-- FileName: NUMBERFORMAT41 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 12.3 -->
  <!-- Creator: David Bertoni -->
  <!-- Purpose: Test of multiple decimal-format elements with identical qualified names.
    This is allowed as long as all attributes are identical (including defaults). -->

<xsl:decimal-format name="foo:decimal1" minus-sign='-' NaN='not a number'/>
<xsl:decimal-format name="baz:decimal1" NaN='not a number' decimal-separator='.'/>

<xsl:template match="doc">
  <out>
    <xsl:text>&#10;</xsl:text>
    <foo>
      <xsl:value-of select="format-number('NaN','###','foo:decimal1')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="format-number(-13.2,'###.0','foo:decimal1')"/>
    </foo>
    <xsl:text>&#10;</xsl:text>
    <baz>
      <xsl:value-of select="format-number('NaN','###','baz:decimal1')"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="format-number(-13.2,'###.0','baz:decimal1')"/>
    </baz>
  </out>
</xsl:template>


  <!--
   * Licensed to the Apache Software Foundation (ASF) under one
   * or more contributor license agreements. See the NOTICE file
   * distributed with this work for additional information
   * regarding copyright ownership. The ASF licenses this file
   * to you under the Apache License, Version 2.0 (the  "License");
   * you may not use this file except in compliance with the License.
   * You may obtain a copy of the License at
   *
   *     http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the License for the specific language governing permissions and
   * limitations under the License.
  -->

</xsl:stylesheet>
