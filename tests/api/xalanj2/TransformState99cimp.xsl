<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- xsl-comment-1 Filename: TransformState99cimp.xsl -->
























  <xsl:template match="pies-are-good" name="apple">
<!-- Note formatting is important, this apply-templates must end on col 99; line number must match expected -->
<!-- This should be line # 30 in the file! 4567-50-234567-60-234567-70-234567-80-234567-90-23456-99 -->
    <apple>
                                                                <xsl:value-of select="count(.)" />
      <xsl:apply-templates select="list" mode="ala" />
    </apple>
  </xsl:template>
     
</xsl:stylesheet>
