<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
>

  <!-- Test FileName: mk023.xsl -->
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
  <!-- Example: booklist.xml, author-key.xsl -->
  <!-- Chapter/Page: 4-227 -->
  <!-- Purpose: Multi-valued Non-unique keys -->

<xsl:key name="author-name" match="book" use="author"/>

<xsl:param name="author" select="'John Vlissides'"/>

<xsl:template match="/">
<xsl:copy-of select="key('author-name', translate($author, '_', ' '))"/>
</xsl:template>

</xsl:stylesheet>

