<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0"
                      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                      xmlns:co="http://acme.com/xslt">


<xsl:variable name="co:company-name" select="'Acme Widgets Incorporated'"/>

<xsl:variable name="co:copyright" 
                      select="concat('Copyright © ', $co:company-name)"/>

</xsl:stylesheet>

