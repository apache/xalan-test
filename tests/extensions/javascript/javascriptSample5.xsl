<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:counter="MyCounter"
                extension-element-prefixes="counter"
                version="1.0">

<!-- Copied from: java/samples/extensions/5-numlistJscript.xsl -->

  <lxslt:component prefix="counter"
                   elements="init incr" functions="read">
    <lxslt:script lang="javascript">
      var counters = new Array();

      function init (xslproc, elem) {
        name = elem.getAttribute ("name");
        value = parseInt(elem.getAttribute ("value"));
        counters[name] = value;
        return null;
      }

      function read (name) {
        return "" + (counters[name]);
      }

      function incr (xslproc, elem)
      {
        name = elem.getAttribute ("name");
        counters[name]++;
        return null;
      }
    </lxslt:script>
  </lxslt:component>

  <xsl:template match="/">
    <HTML>
      <H1>JavaScript Example.</H1>
      <counter:init name="index" value="1"/>
      <p>Here are the names in alphabetical order by last name:</p>
      <xsl:for-each select="doc/name">
        <xsl:sort select="@last"/>
        <xsl:sort select="@first"/>
        <p>
        <xsl:text>[</xsl:text>
        <xsl:value-of select="counter:read('index')"/>
        <xsl:text>]. </xsl:text>
        <xsl:value-of select="@last"/>
        <xsl:text>, </xsl:text>
        <xsl:value-of select="@first"/>
        </p>
        <counter:incr name="index"/>
      </xsl:for-each>
    </HTML>
  </xsl:template>
 

  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
