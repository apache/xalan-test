<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- FileName: idkeyerr20 -->
  <!-- Document: http://www.w3.org/TR/xslt -->
  <!-- DocVersion: 19991116 -->
  <!-- VersionDrop: 1.1 will outlaw this trick. -->
  <!-- Creator: David Marston -->
  <!-- Section: 12.2 -->
  <!-- Purpose: Test for xsl:key that uses key() on a different keyspace in its match attribute. -->
  <!-- ExpectedException: recursive key() calls are not allowed -->

<xsl:key name="allowdiv" match="div" use="@allow"/>
<xsl:key name="titles" match="key('allowdiv','yes')" use="title"/>

<xsl:template match="doc">
 <root>
  <xsl:value-of select="key('titles', 'Introduction')/p"/>
  <xsl:value-of select="key('titles', 'Stylesheet Structure')/p"/>
  <!-- The next one is an empty node-set -->
  <xsl:value-of select="key('titles', 'Expressions')/p"/>
 </root>
</xsl:template>

</xsl:stylesheet>
