<?xml version="1.0" ?> 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- "computational (recursive) template is very slow compared to xalan 1.2.2" c.mallwitz@intershop.de 

jkesselm May2023 notes:
jkesselm May2023 notes:
     To actually evaluate this complaint, we would need to run the
     stylesheet under both Xalan 1 and Xalan 2 and compare the results.
     The comment has not given us any information about the configuration(s) in
     which the performance difference was noted, nor whether the issue appears
     to be stylesheet preparation or execution. 

     Java performance is something of a black art, especially under
     hotspot JVMs. We do have a tests/perf collection. but using it
     requires running both old and new versions of the code in
     otherwise-identical environments. We could make checking for performance
     regression part of the release process, and perhaps should.
-->
  <xsl:template match="/">
    <foo>
    <xsl:call-template name="func1">
      <xsl:with-param name="list" select="//x" /> 
    </xsl:call-template>
    </foo>
  </xsl:template>

  <xsl:template name="func1">
    <xsl:param name="list" /> 
    <xsl:choose>
      <xsl:when test="$list">
        <xsl:call-template name="func1">
          <xsl:with-param name="list" select="$list[position()!=1]" /> 
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>0</xsl:otherwise> 
    </xsl:choose>
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
