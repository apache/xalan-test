<?xml version="1.0" encoding="ISO-8859-1"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

  <!-- FileName: entref01 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- Section: 16.2 HTML Output Method -->
  <!-- Purpose: ESC of non-ASCII chars in URI attribute	values using method 
       cited in Section B.2.1 of HTML 4.0 Spec. -->

<xsl:template match="/">
  <out>

    1. "&amp;"   <A HREF="&amp;"/>
    2. "&lt;"    <A href="&lt;"/>
    3. "&gt;"    <A href="&gt;"/>
    4. "&quot;"  <A href="&quot;"/>
    5. "&apos;"  <A href="&apos;"/>
    6. "&#169;"  <A HREF="&#169;"/>	<!-- Copyright -->
    7. "&#035;"  <A href="&#035;"/>	<!-- Hashmark -->
    8. "&#165;"  <A href="&#165;"/>	<!-- yen      -->
    9. "&#032;"  <A href="&#032;"/>	<img src="Test 31 Gif.gif"/>
   10."&#037;"  <A href="&#037;"/>	<!-- percent -->
   11."&#009;"  <A href="&#009;"/>	<!-- tab    -->
   12."&#127;"  <A href="&#127;"/>	<!-- delete  -->
   13."&#209;"  <A href="&#209;"/>	<!-- N-tilde -->
   14."&#338;"  <A href="&#338;"/>  <!-- OE Ligature -->
   <A href="phnix.html">test1</A>
      <A href="phŒnix.html">test2</A> 
  </out>
</xsl:template>
 
</xsl:stylesheet>
