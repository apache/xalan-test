<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- Test FileName: mk025.xsl -->
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
  <!-- Example: poem.xml, poem.xsl -->
  <!-- Chapter/Page: 4-247 -->
  <!-- Purpose: Numbering the lines of a poem -->

<xsl:template match="/">
<html><body>
<p><xsl:apply-templates select="/poem/stanza"/></p>
</body></html>
</xsl:template>

<xsl:template match="stanza">
<p><table><xsl:apply-templates/></table></p>
</xsl:template> 

<xsl:template match="line">
<tr>
<td width="350"><xsl:value-of select="."/></td>
<td width="50">
   <xsl:variable name="line-nr">
      <xsl:number level="any" from="poem"/>
   </xsl:variable>
   <xsl:if test="$line-nr mod 3 = 0">
      <xsl:value-of select="$line-nr"/>
   </xsl:if>
</td>
</tr>
</xsl:template>

</xsl:stylesheet>

