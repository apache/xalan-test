<?xml version="1.0"?> 
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math">

<!-- Test math:cos() -->

<xsl:variable name="zero" select="0"/>
<xsl:variable name="nzero" select="-0"/>
<xsl:variable name="num1" select="1.99"/>
<xsl:variable name="num2" select="3.1428475"/>
<xsl:variable name="temp1" select="-7"/>
<xsl:variable name="temp2" select="-9.99999"/>
<xsl:variable name="rad1" select="1.0"/>
<xsl:variable name="rad2" select="25"/>
<xsl:variable name="rad3" select="0.253"/>
<xsl:variable name="rad4" select="-0.888"/>
<xsl:variable name="input1" select="number(//number[1])"/>
<xsl:variable name="input2" select="number(//number[2])"/>
<xsl:variable name="input3" select="$input1 div $zero"/>

<xsl:template match="/">
   <out>
      Cos value of zero is:
      <xsl:value-of select="math:cos($zero)"/><br/> 
      Cos value of nzero is:
      <xsl:value-of select="math:cos($nzero)"/><br/> 
      Cos value of num1 is:
      <xsl:value-of select="math:cos($num1)"/><br/> 
      Cos value of num2 is:
      <xsl:value-of select="math:cos($num2)"/><br/> 
      Cos value of temp1 is:
      <xsl:value-of select="math:cos($temp1)"/><br/> 
      Cos value of temp2 is:
      <xsl:value-of select="math:cos($temp2)"/><br/> 
      Cos value of rad1 is: 
      <xsl:value-of select="math:cos($rad1)"/><br/> 
      Cos value of rad2 is:
      <xsl:value-of select="math:cos($rad2)"/><br/> 
      Cos value of rad3 is:
      <xsl:value-of select="math:cos($rad3)"/><br/> 
      Cos value of rad4 is:
      <xsl:value-of select="math:cos($rad4)"/><br/> 
      Cos value of input1 number is:
      <xsl:value-of select="math:cos($input1)"/><br/> 
      Cos value of input2 number is:
      <xsl:value-of select="math:cos($input2)"/><br/> 
      Cos value of input3 number is:
      <xsl:value-of select="math:cos($input3)"/>
      
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
