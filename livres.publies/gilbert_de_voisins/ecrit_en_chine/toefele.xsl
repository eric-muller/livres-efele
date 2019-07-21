<?xml version='1.0' encoding='UTF-8'?>

<xsl:stylesheet 
  xmlns="http://efele.net/2010/ns/bml"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0">

<xsl:import href="../../../tools/toefele.xsl"/>

<xsl:output 
  method="xml"
  indent="no"
  encoding="UTF-8"/>


<xsl:template match='bml:photo/bml:titre'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='bml:photo/bml:date'>
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match='bml:photo'>
  <div class='photo'>
    <p><img src='{@src}' alt='{title}'/></p>
    <p class='smaller c'><xsl:apply-templates select='bml:titre'/><xsl:if test='bml:date'> — <xsl:apply-templates select='bml:date'/></xsl:if></p>
  </div>
</xsl:template>


<xsl:template match='bml:entree-lieu'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='bml:entree-date'>
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match='bml:entree'>
  <h3><i><xsl:apply-templates select='bml:entree-lieu'/> — <xsl:apply-templates select='bml:entree-date'/></i></h3>

  <xsl:apply-templates select='bml:photo'/>

  <xsl:apply-templates select='*[not(self::bml:entree-lieu) and not(self::bml:entree-date) and not(self::bml:photo)]'/>

</xsl:template>



</xsl:stylesheet>
