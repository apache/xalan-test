<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:import href="../main_import.xsl"/>

<!-- FileName: impinclman19 -->
<!-- Document: http://www.w3.org/TR/xslt -->
<!-- DocVersion: 19991116 -->
<!-- Section: 2.6.2 Stylesheet Import -->
<!-- Creator: Vikranth Reddy (vreddy@covigo.com) -->
<!-- Purpose: Test of import (going up directory tree) using ..\main_import.xsl -->


<xsl:template match="test">
In Frag-Subdir: <xsl:value-of select="."/>
<xsl:apply-imports/>
</xsl:template>

</xsl:stylesheet>