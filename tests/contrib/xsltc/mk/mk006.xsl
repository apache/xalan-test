<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		version="1.0">
  
  <!-- Test FileName: mk006.xsl -->
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
  <!-- Example:  books.xml, avt.xsl -->
  <!-- Chapter/Page: 3-113 -->
  <!-- Purpose: Using Attribute Value Template in Literal Result Element -->

<xsl:output indent="yes"/>
<xsl:template match="/">
<xsl:for-each select="//book">
<div id="div{position()}">
<xsl:value-of select="title"/>
</div>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
