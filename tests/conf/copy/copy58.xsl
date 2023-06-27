<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: copy58 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- AdditionalSpec: http://www.w3.org/1999/11/REC-xslt-19991116-errata/#E27 -->
  <!-- Section: 11.3 Using Values of Variables & Parameters with xsl:copy-of. -->
  <!-- Creator: David Marston -->
  <!-- Purpose: Use copy-of to put a node-set in an attribute, where some members are text nodes. -->
  <!-- Invalid nodes (non-text) and their content should be ignored. -->

  <!-- Per XSLT 1.0 section 7.1.5: "The following are all errors:
       [...]  Creating nodes other than text nodes during the
       instantiation of the content of the xsl:attribute element;
       implementations may either signal the error or ignore the
       offending nodes." Implied by handling of PIs and comments: if
       nodes are ignored, their content must also be ignored. In other
       words, simply using string-value of the content is *not*
       correct. AS OF VERSION 2.7.3, XALAN IS GETTING THIS WRONG. -->

<xsl:output method="xml" indent="no" encoding="UTF-8"/>

<xsl:template match="/">
  <out>
    <xsl:attribute name="attr1">
      <xsl:copy-of select="docs/a/node()"/>
    </xsl:attribute>
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
