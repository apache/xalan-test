<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
>

  <!-- Test FileName: mk042.xsl -->
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
  <!-- Example: booklist.xml, format-names.xsl -->
  <!-- Chapter/Page: 7-480 -->
  <!-- Purpose: Formatting a list using position and last functions -->

<xsl:template match="book">
<auth>
    <xsl:for-each select="author">
        <xsl:value-of select="."/>
        <xsl:choose>
            <xsl:when test="position() = last()"/>
            <xsl:when test="position() = last()-1"> and </xsl:when>
            <xsl:otherwise>, </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
</auth>
</xsl:template>

</xsl:transform>

