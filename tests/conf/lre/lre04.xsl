<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:bdd="http://buster.com"
    xmlns:jad="http://administrator.com">

  <!-- FileName: lre04 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.1.1 Literal Result Elements-->
  <!-- Creator: Paul Dick -->
  <!-- Purpose: The designation of a namespace as an excluded namespace is 
       effective within the subtree of the stylesheet rooted at the element 
       bearing the exclude-result-prefixes or xsl:exclude-result-prefixes attribute. -->

<xsl:template match="doc">
  <out x="by the corner"><xsl:text>&#010;</xsl:text>
  <sits x="little jack horner" xsl:exclude-result-prefixes="jad"/><xsl:text>&#010;</xsl:text> 
  <minding x="his peas and queues" xsl:exclude-result-prefixes="jad bdd"/></out>
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
