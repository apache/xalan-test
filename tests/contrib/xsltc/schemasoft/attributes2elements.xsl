<?xml version="1.0"?>

<!-- Replace attributes with elements.

     Note that the reverse transform is not possible.



Copyright J.M. Vanel 2000 - under GNU public licence 

xt testHREF.xml attributes2elements.xslt > attributes2elements.xml



 -->



<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'

                version="1.0" >



 <xsl:template match ="@*" >

   <xsl:element name='{name()}'>

    <xsl:value-of select='.' />

   </xsl:element>

 </xsl:template>



 <xsl:template match="*">

  <xsl:copy>

    <xsl:apply-templates select="*|@*" />

  </xsl:copy>

 </xsl:template>

  <!-- Copyright 1999-2004 The Apache Software Foundation.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and limitations under the License. -->

</xsl:stylesheet>
