<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"
            doctype-public="-//W3C//DTD HTML 4.0 Transitional"/>

  <!-- FileName: outputerr03 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 7.3 Creating Processing Instructions -->
  <!-- Purpose: Try to create processing instruction with "xml" as name. -->
  <!-- ExpectedException: processing-instruction name can not be 'xml' -->

<xsl:template match="/">
 <HTML>
   <xsl:processing-instruction name="xml">href="book.css" type="text/css"</xsl:processing-instruction>
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
