<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   version="1.0">

  <!-- Test FileName: mk053.xsl -->
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
  <!-- Example: booklist.xml, booksales.xsl -->
  <!-- Chapter/Page: 8-555 -->
  <!-- Purpose: Finding the total sales value -->
  
<xsl:template name="total-sales-value">
   <xsl:param name="list"/>
   <xsl:choose>
      <xsl:when test="$list">
         <xsl:variable name="first" select="$list[1]"/>
         <xsl:variable name="total-of-rest">
            <xsl:call-template name="total-sales-value">
               <xsl:with-param name="list" select="$list[position()!=1]"/>
            </xsl:call-template>
         </xsl:variable>
         <xsl:value-of select="$first/sales * $first/price + $total-of-rest"/>
      </xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
   </xsl:choose>
</xsl:template>


<xsl:template match="/">   
   <xsl:variable name="total">
        <xsl:call-template name="total-sales-value">
            <xsl:with-param name="list" select="//book"/>
        </xsl:call-template>
   </xsl:variable>
Total sales value is: <xsl:value-of select="format-number($total, '$#.00')"/>   
</xsl:template>


</xsl:stylesheet>
