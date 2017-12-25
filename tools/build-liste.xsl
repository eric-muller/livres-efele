<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="#all"
  version="2.0">

<xsl:param name='ext'/>
<xsl:param name='desc'/>

<xsl:output method='html'/>

<xsl:template match='/'>
<html>
<body>
<p>Liste des réimpressions ÉFÉLÉ: <xsl:value-of select='$desc'/>.</p>

<ul>
<xsl:for-each select="tokenize(unparsed-text('liste.txt'), '\r?\n')">
  <li><a href='livres/{.}{$ext}'><xsl:value-of select='.'/></a></li>
</xsl:for-each>
</ul>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
