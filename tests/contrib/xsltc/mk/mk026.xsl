<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
>

  <!-- Test FileName: mk026.xsl -->
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
  <!-- Example: poem.xml, call.xsl -->
  <!-- Chapter/Page: 4-265 -->
  <!-- Purpose: Using xsl:param with a default value -->

<xsl:output method="text"/>

<xsl:template match="/">
<xsl:for-each select="//*">
    <xsl:value-of select="concat(name(), ' -- ')"/>
    <xsl:call-template name="depth"/>;
</xsl:for-each>
</xsl:template>

<xsl:template name="depth">
    <xsl:param name="node" select="."/>
    <xsl:value-of select="count($node/ancestor::node())"/>
</xsl:template>


</xsl:transform>

