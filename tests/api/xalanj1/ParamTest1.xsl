<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: ParamTest1.xsl -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19990922 -->
  <!-- Section: 11.2 Values of Variables and Parameters  -->
  <!-- Purpose: Verify xsl:param in stylesheet root, either set or not set via setStylesheetParam -->

<xsl:param name="p1">
    <B>ABC</B>
</xsl:param>
<xsl:param name="p2">
    <B>DEF</B>
</xsl:param>

<xsl:param name="t1">notset</xsl:param>


<xsl:param name="s1" select="'s1val'">
</xsl:param>
<xsl:param name="s2" select="'s2val'">
</xsl:param>

<xsl:template match="doc">
  <xsl:param name="p3">
    <B>GHI</B>
  </xsl:param>
  <xsl:param name="s3" select="'s3val'">
  </xsl:param>
   <outp>
     <xsl:value-of select="$p1"/><xsl:text>,</xsl:text><xsl:copy-of select="$p1"/><xsl:text>; </xsl:text>
     <xsl:value-of select="$p2"/><xsl:text>,</xsl:text><xsl:copy-of select="$p2"/><xsl:text>; </xsl:text>
     <xsl:value-of select="$p3"/><xsl:text>,</xsl:text><xsl:copy-of select="$p3"/><xsl:text>; </xsl:text>
   </outp>
   <xsl:text>:
   </xsl:text>
   <outs>
     <xsl:value-of select="$s1"/><xsl:text>,</xsl:text><xsl:copy-of select="$s1"/><xsl:text>; </xsl:text>
     <xsl:value-of select="$s2"/><xsl:text>,</xsl:text><xsl:copy-of select="$s2"/><xsl:text>; </xsl:text>
     <xsl:value-of select="$s3"/><xsl:text>,</xsl:text><xsl:copy-of select="$s3"/><xsl:text>; </xsl:text>
   </outs>
   <xsl:text>:
   </xsl:text>
   <outt>
     <xsl:value-of select="$t1='notset'"/><xsl:text>,</xsl:text>
     <xsl:value-of select="$t1=''"/><xsl:text>,</xsl:text>
     <xsl:value-of select="$t1='a'"/><xsl:text>,</xsl:text>
     <xsl:value-of select="$t1='1'"/><xsl:text>,</xsl:text>
     <xsl:value-of select="$t1"/>
   </outt>
   
</xsl:template>
  
</xsl:stylesheet>