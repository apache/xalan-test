<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0" >

<!-- User mark@ssglimited.com (Mark Peterson) claims in Xalan-J 2.2.0:
The XSL file, below, should make $flights= {"1","2"}, but it contains {"1"} - 
when using the XML example file shown below.
xml-xalan CVS 11-Feb-02 9AM returns <out>1<br/></out> -sc

jkesselm May2023 notes:
jkesselm May2023 notes:

The question is whether the first of these
  <colData colId="L">2</colData>
  <colData colId="F">2</colData> 
should affect whether the second is selected... ie, what 
not(.=preceding::colData) should be reporting.

Per XPath 1.0 section 3.4, for equality expressions: "If both objects
to be compared are node-sets, then the comparison will be true if and
only if there is a node in the first node-set and a node in the second
node-set such that the result of performing the comparison on the
string-values of the two nodes is true."

XPath further says "The string-value of an element node is the
concatenation of the string-values of all text node descendants of the
element node in document order."

So the first colData be rejected by @colId="F" being false. And the
second should test not("2"="2") and the node should be rejected
for that reason.

"If you delete the first it works"... well, if you delete the first
then the second is not rejected by that (not) test against the
previous instance, and is accepted.

That may not be what the user intended, but to my out-of-practice eye
this appears to be Working As Designed. Move to reject this issue.
-->

<xsl:variable name="flights" select="/report/colData[@colId='F' and not(.=preceding::colData)]"/>

<xsl:template match="/report">
<out>
    <xsl:for-each select="$flights">
        <xsl:value-of select="." /><br />
    </xsl:for-each>
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
