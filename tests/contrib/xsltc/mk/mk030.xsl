<?xml version="1.0" ?>
<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   version="1.0">

  <!-- Test FileName: mk030.xsl -->
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
  <!-- Example: opera.xml, opera.xsl -->
  <!-- Chapter/Page: 4-312 -->
  <!-- Purpose: Using a variable for context sensitive variables -->

<xsl:output method="html" indent="yes" />
<xsl:template match="/">
<html>
  <body>
    <center>
     <h1>Programme</h1>
     <xsl:for-each select="/programme/composer">
       <h2><xsl:value-of select="concat(fullname, ' (', born, '-', died, ')')"/></h2>
       <xsl:variable name="c" select="."/>
       <xsl:for-each select="/programme/opera[composer=$c/@name]">
           <p><xsl:value-of select="title"/></p>
       </xsl:for-each>
     </xsl:for-each>
    </center>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
