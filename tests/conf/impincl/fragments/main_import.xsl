<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:import href="../main_import.xsl"/>

<!-- FileName: impinclman99 -->
<!-- Document: http://www.w3.org/TR/xslt -->
<!-- DocVersion: 19991116 -->
<!-- Section: 2.6.2 Stylesheet Import -->
<!-- Creator: Vikranth Reddy (vreddy@covigo.com) -->
<!-- Purpose: Test of import (going up directory tree) using ..\main_import.xsl -->
<!--          This is the last file in the import chain. Main xsl file is in -->
<!--          xmanual/redirect/subdir  -->

<xsl:template match="test">
In Fragments: <xsl:value-of select="."/>
<xsl:apply-imports/>
</xsl:template>

</xsl:stylesheet>