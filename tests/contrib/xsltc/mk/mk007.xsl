<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		version="1.0">
  
  <!-- Test FileName: mk007.xsl -->
  <!-- Source Attribution: 
       This test was written by Michael Kay and is taken from 
       'XSLT Programmer's Reference' published by Wrox Press Limited in 2000;
       ISBN 1-861003-12-9; copyright Wrox Press Limited 2000; all rights reserved. 
       Now updated in the second edition (ISBN 1861005067), http://www.wrox.com.
       No part of this book may be reproduced, stored in a retrieval system or 
       transmitted in any form or by any means - electronic, electrostatic, mechanical, 
       photocopying, recording or otherwise - without the prior written permission of 
       the publisher, except in the case of brief quotations embodied in critical articles or reviews.
  -->
  <!-- Example: sample.xml, principal.xsl (includes date.xsl, copyright.xsl) -->
  <!-- Chapter/Page: 3-91 -->
  <!-- Purpose: Including stylesheets -->

<!--<xsl:include href="./inc/date.xsl"/> -->
<xsl:variable name="date" select="'2001'" />
<xsl:include href="./inc/copyright.xsl"/>

<xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>
<xsl:strip-space elements="*"/>

<xsl:template match="date">
    <date><xsl:value-of select="$date"/></date>
</xsl:template>

<xsl:template match="copyright">
    <copyright><xsl:call-template name="copyright"/></copyright>
</xsl:template>

<xsl:template match="*">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>

