<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- Test FileName: mk028.xsl -->
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
  <!-- Example: soloist.xml, soloist.xsl -->
  <!-- Chapter/Page: 4-295 -->
  <!-- Purpose: Rules-based processing of template rules -->

<xsl:template match="/">
<html><body>
<xsl:apply-templates/>
</body></html>
</xsl:template>


<xsl:template match="para">
   <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="publication">
   <font face="arial"><xsl:apply-templates/></font>
</xsl:template>

<xsl:template match="quote">
   <xsl:text/>"<xsl:apply-templates/>"<xsl:text/>
</xsl:template>

<xsl:template match="work">
   <i><xsl:apply-templates/></i>
</xsl:template>

<xsl:template match="role">
   <u><xsl:apply-templates/></u>
</xsl:template>

</xsl:stylesheet>
