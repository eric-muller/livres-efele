<?xml version='1.0' encoding='UTF-8'?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml="http://efele.net/2010/ns/bml"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns="http://efele.net/2010/ns/bml"
  version="2.0">

<xsl:output 
  method="xml"
  indent="no"
  encoding="UTF-8"/>

<xsl:include href="../../../tools/bml-common.xsl"/>


<xsl:param name='tirage'></xsl:param>

<xsl:template match="@*|node()" mode="copy">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="copy"/>
  </xsl:copy>
</xsl:template>


<xsl:template match='bml:bml'>
  <bml>
    <xsl:apply-templates select='bml:metadata' mode='copy'/>

    <toc>
      <xsl:apply-templates select='bml:toc/bml:tocentry' mode='copy'/>
      <tocentry label="Colophon" idref="colophon"/>
    </toc>

    <xsl:apply-templates select='bml:cover' mode='copy'/>

    <page-sequences>

      <xsl:apply-templates select='bml:page-sequences/bml:page-sequence' mode='copy'/>

      <xsl:call-template name='colophon'/>

    </page-sequences>
  </bml>
</xsl:template>


<xsl:template name='illustrations-toc'>
  <tocentry label="Illustrations" idref="illustrations">
    <xsl:apply-templates select="//bml:img[@class='fullpageimage']" mode="toc3"/>
  </tocentry>
</xsl:template>

<xsl:template match="bml:img[@class='fullpageimage']" mode="toc3">
  <tocentry label="{@alt}" idref="img.{generate-id()}"/>
</xsl:template>


<xsl:template name='illustrations-body'>
  <page-sequence>
    <div id="illustrations">
      <h1>ILLUSTRATIONS</h1>

      <xsl:apply-templates select="//bml:img[@class='fullpageimage']" mode="toc2"/>
    </div>
  </page-sequence>
</xsl:template>

<xsl:template match="bml:img[@class='fullpageimage']" mode="toc2">
  <p><a idref="img.{generate-id()}"><xsl:value-of select='@alt'/></a></p>
</xsl:template>




<xsl:template match='bml:dedication'>
  <page-sequence>
    <div>
    <xsl:apply-templates/>
    </div>
  </page-sequence>
</xsl:template>


<xsl:template match='bml:div[@class="chapter"] | bml:bml/bml:div' priority='3'>
  <xsl:variable name='toc' select='@toc'/>
  <xsl:variable name='id' select='generate-id()'/>

  <xsl:for-each-group
      select='*'
      group-adjacent='if (self::bml:img) then "img" else if (self::bml:div[@class="chapter"]) then "chapter" else if (self::bml:threehundredk) then "threehundredk" else ""'>

    <xsl:choose>
      <xsl:when test='current-grouping-key() = "threehundredk"'>
        <!-- nothing to generate -->
      </xsl:when>
      <xsl:when test='current-grouping-key() = "chapter"'>
        <xsl:apply-templates select='current-group()'/>
      </xsl:when>
      <xsl:otherwise>
        <page-sequence>
          <div>
            <xsl:if test='position() = 1'>
              <xsl:attribute name='id' select='$id'/>
            </xsl:if>
            <xsl:apply-templates select='current-group()'/>
          </div>
        </page-sequence>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each-group>

</xsl:template>



<xsl:template match='bml:img'>
  <img id='img.{generate-id()}' src='{@src}' alt='{@alt}'>
    <xsl:if test='@class'>
      <xsl:attribute name='class'><xsl:value-of select='@class'/></xsl:attribute>
    </xsl:if>
  </img>

</xsl:template>



<xsl:template name='colophon'>
  <page-sequence>
    <div id='colophon'>
      <h2>COLOPHON</h2>
  
    <p>Ce volume est le 
    <xsl:choose>
      <xsl:when test='/bml:bml/@volume="1"'>premier</xsl:when>
      <xsl:when test='/bml:bml/@volume="2"'>deuxième</xsl:when>
      <xsl:when test='/bml:bml/@volume="3"'>troisième</xsl:when>
      <xsl:when test='/bml:bml/@volume="4"'>quatrième</xsl:when>
      <xsl:when test='/bml:bml/@volume="5"'>cinquième</xsl:when>
      <xsl:when test='/bml:bml/@volume="6"'>sixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="7"'>septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="8"'>huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="9"'>neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="10"'>dixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="11"'>onzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="12"'>douzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="13"'>treizième</xsl:when>
      <xsl:when test='/bml:bml/@volume="14"'>quatorzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="15"'>quinzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="16"'>seizième</xsl:when>
      <xsl:when test='/bml:bml/@volume="17"'>dix-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="18"'>dix-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="19"'>dix-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="20"'>vingtième</xsl:when>
      <xsl:when test='/bml:bml/@volume="21"'>vingt-et-unième</xsl:when>
      <xsl:when test='/bml:bml/@volume="22"'>vingt-deuxième</xsl:when>
      <xsl:when test='/bml:bml/@volume="23"'>vingt-troisième</xsl:when>
      <xsl:when test='/bml:bml/@volume="24"'>vingt-quatrième</xsl:when>
      <xsl:when test='/bml:bml/@volume="25"'>vingt-cinquième</xsl:when>
      <xsl:when test='/bml:bml/@volume="26"'>vingt-sixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="27"'>vingt-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="28"'>vingt-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="29"'>vingt-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="30"'>trentième</xsl:when>
      <xsl:when test='/bml:bml/@volume="31"'>trente-et-unième</xsl:when>
      <xsl:when test='/bml:bml/@volume="32"'>trente-deuxième</xsl:when>
      <xsl:when test='/bml:bml/@volume="33"'>trente-troisième</xsl:when>
      <xsl:when test='/bml:bml/@volume="34"'>trente-quatrième</xsl:when>
      <xsl:when test='/bml:bml/@volume="35"'>trente-cinquième</xsl:when>
      <xsl:when test='/bml:bml/@volume="36"'>trente-sixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="37"'>trente-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="38"'>trente-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="39"'>trente-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="40"'>quarantième</xsl:when>
      <xsl:when test='/bml:bml/@volume="41"'>quarante-et-unième</xsl:when>
      <xsl:when test='/bml:bml/@volume="42"'>quarante-deuxième</xsl:when>
      <xsl:when test='/bml:bml/@volume="43"'>quarante-troisième</xsl:when>
      <xsl:when test='/bml:bml/@volume="44"'>quarante-quatrième</xsl:when>
      <xsl:when test='/bml:bml/@volume="45"'>quarante-cinquième</xsl:when>
      <xsl:when test='/bml:bml/@volume="46"'>quarante-sixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="47"'>quarante-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="48"'>quarante-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="49"'>quarante-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="50"'>cinquantième</xsl:when>
      <xsl:when test='/bml:bml/@volume="51"'>cinquante-et-unième</xsl:when>
      <xsl:when test='/bml:bml/@volume="52"'>cinquante-deuxième</xsl:when>
      <xsl:when test='/bml:bml/@volume="53"'>cinquante-troisième</xsl:when>
      <xsl:when test='/bml:bml/@volume="54"'>cinquante-quatrième</xsl:when>
      <xsl:when test='/bml:bml/@volume="55"'>cinquante-cinquième</xsl:when>
      <xsl:when test='/bml:bml/@volume="56"'>cinquante-sixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="57"'>cinquante-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="58"'>cinquante-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="59"'>cinquante-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="60"'>soixantième</xsl:when>
      <xsl:when test='/bml:bml/@volume="61"'>soixante-et-unième</xsl:when>
      <xsl:when test='/bml:bml/@volume="62"'>soixante-deuxième</xsl:when>
      <xsl:when test='/bml:bml/@volume="63"'>soixante-troisième</xsl:when>
      <xsl:when test='/bml:bml/@volume="64"'>soixante-quatrième</xsl:when>
      <xsl:when test='/bml:bml/@volume="65"'>soixante-cinquième</xsl:when>
      <xsl:when test='/bml:bml/@volume="66"'>soixante-sixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="67"'>soixante-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="68"'>soixante-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="69"'>soixante-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="70"'>soixante-dixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="71"'>soixante-et-onzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="72"'>soixante-douzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="73"'>soixante-treizième</xsl:when>
      <xsl:when test='/bml:bml/@volume="74"'>soixante-quatorzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="75"'>soixante-quinzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="76"'>soixante-seizième</xsl:when>
      <xsl:when test='/bml:bml/@volume="77"'>soixante-dix-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="78"'>soixante-dix-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="79"'>soixante-dix-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="80"'>quatre-vingtième</xsl:when>
      <xsl:when test='/bml:bml/@volume="81"'>quatre-vingt-unième</xsl:when>
      <xsl:when test='/bml:bml/@volume="82"'>quatre-vingt-deuxième</xsl:when>
      <xsl:when test='/bml:bml/@volume="83"'>quatre-vingt-troisième</xsl:when>
      <xsl:when test='/bml:bml/@volume="84"'>quatre-vingt-quatrième</xsl:when>
      <xsl:when test='/bml:bml/@volume="85"'>quatre-vingt-cinquième</xsl:when>
      <xsl:when test='/bml:bml/@volume="86"'>quatre-vingt-sixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="87"'>quatre-vingt-septième</xsl:when>
      <xsl:when test='/bml:bml/@volume="88"'>quatre-vingt-huitième</xsl:when>
      <xsl:when test='/bml:bml/@volume="89"'>quatre-vingt-neuvième</xsl:when>
      <xsl:when test='/bml:bml/@volume="90"'>quatre-vingt-dixième</xsl:when>
      <xsl:when test='/bml:bml/@volume="91"'>quatre-vingt-onzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="92"'>quatre-vingt-douzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="93"'>quatre-vingt-treizième</xsl:when>
      <xsl:when test='/bml:bml/@volume="94"'>quatre-vingt-quatorzième</xsl:when>
      <xsl:when test='/bml:bml/@volume="95"'>quatre-vingt-quinzième</xsl:when>
    </xsl:choose>
    de la réimpression <s>ÉFÉLÉ</s> de la Comédie Humaine. Le texte de référence est 
    <xsl:choose>
      <xsl:when test="/bml:bml/@volume &lt;= 11">l’édition Furne, volume 1 (1842), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>ZVoOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 18">l’édition Furne, volume 2 (1842), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>2YoTAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 20">l’édition Furne, volume 3 (1842), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>2ooTAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 21">l’édition Furne, volumes 3 (1842) et 4 (1845), disponibles à <xsl:call-template name='google'><xsl:with-param name='gid'>2ooTAAAAQAAJ</xsl:with-param></xsl:call-template> et <xsl:call-template name='google'><xsl:with-param name='gid'>f1oOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 25">l’édition Furne, volume 4 (1845), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>f1oOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 28">l’édition Furne, volume 5 (1843), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>24oTAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 32">l’édition Furne, volume 6 (1843), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>3loOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 35">l’édition Furne, volume 7 (1844), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>D1EOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 38">l’édition Furne, volume 8 (1843), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>NlEOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 42">l’édition Furne, volume 9 (1843), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>6VoOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 48">l’édition Furne, volume 10 (1844), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>g1EOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 54">l’édition Furne, volume 11 (1844), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>HVsOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 55">l’édition Furne, volume 12 (1846), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>DlIOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 56">l’édition Houssiaux, volume 18 (1855), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>VCRAAAAAYAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 60">l’édition Furne, volume 12 (1846), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>DlIOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 62">l’édition Furne, volume 17 (1848), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>uo8TAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 65">l’édition Furne, volume 12 (1846), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>DlIOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 66">l’édition Furne, volume 12 (1846) et l’édition Houssiaux, volume 18 (1855), disponibles à <xsl:call-template name='google'><xsl:with-param name='gid'>DlIOAAAAQAAJ</xsl:with-param></xsl:call-template> et <xsl:call-template name='google'><xsl:with-param name='gid'>VCRAAAAAYAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 70">l’édition Furne, volume 13 (1845), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>dF0OAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 71">l’édition Houssiaux, volume 18 (1855), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>VCRAAAAAYAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 76">l’édition Furne, volume 14 (1845), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>N1IOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 87">l’édition Furne, volume 15 (1845), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>qV0OAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 88">l’édition Furne, volumes 15 (1845) et 16 (1846), disponibles à <xsl:call-template name='google'><xsl:with-param name='gid'>qV0OAAAAQAAJ</xsl:with-param></xsl:call-template> et <xsl:call-template name='google'><xsl:with-param name='gid'>fVIOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 92">l’édition Furne, volume 16 (1846), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>fVIOAAAAQAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:when test="/bml:bml/@volume &lt;= 93">l’édition Houssiaux, volume 18 (1855), disponible à <xsl:call-template name='google'><xsl:with-param name='gid'>VCRAAAAAYAAJ</xsl:with-param></xsl:call-template>.</xsl:when>
      <xsl:otherwise>(inconnu).</xsl:otherwise>
    </xsl:choose></p>

        <vsep class="emptyline"/>

    <p>Les erreurs
    orthographiques et typographiques de cette édition sont
    indiquées entre crochets : « accomplissant [accomplisant] »
    Toutefois, les orthographes normales pour l’époque ou pour Balzac (« collége », « long-temps ») ne sont pas corrigées.</p>

        <vsep class="emptyline"/>

    <p>Ce tirage <xsl:value-of select='$tirage'/> a été fait le
    <xsl:call-template name='tirage-date'/>. D’autres tirages sont
    disponibles à <i><a
    href='http://efele.net/ebooks'>http://​efele.net/​ebooks</a></i>.</p>

        <vsep class="emptyline"/>

    
    <p>Cette numérisation a été obtenue en
    réconciliant :</p>
    
    <p>― l’édition critique en ligne du Groupe
    International de Recherches Balzaciennes, Groupe ARTFL
    (Université de Chicago), Maison de Balzac (Paris) :
    <a href="http://www.v2asp.paris.fr/commun/v2asp/musees/balzac/furne/presentation.htm"><i>http://&#x200b;www.v2asp.paris.fr/&#x200b;commun/&#x200b;v2asp/&#x200b;musees/&#x200b;balzac/&#x200b;furne/&#x200b;presentation.htm</i></a></p>
    
    <p>― l’ancienne édition du groupe Ebooks Libres et
    Gratuits : <a href="http://www.ebooksgratuits.org"><i>http://www.ebooksgratuits.org</i></a></p>
    
    <p>― l’édition Furne scannée
    par Google Books : <a href="http://books.google.com"><i>http://books.google.com</i></a></p>
    
    <p>— l’édition de <a href="https://ebalzac.com">http://ebalzac.com.</a></p>

    <p>Merci à ces groupes de fournir gracieusement leur travail.</p>

        <vsep class="emptyline"/>

    <p>Si vous trouvez des erreurs, merci de les signaler à <a href="mailto:eric.muller@efele.net"><i>eric.muller@efele.net</i></a>. Merci à Fred, Coolmicro, PatriceC, Nicolas Taffin, Inês Arigoni, Célia Tran Van Huong, Jean-Guy Le Duigou, Pierre Periac et plus particulièrement Jacques Quintallet pour les erreurs qu’ils ont signalées.</p>
  
  </div>
  </page-sequence>
</xsl:template>



<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

  




<xsl:template match='bml:div[@toc]' mode='toc'>
  <tocentry label='{@toc}' idref='{generate-id()}'>
    <xsl:apply-templates mode='toc'/>
  </tocentry>
</xsl:template>

<xsl:template match='node()' mode='toc'>
  <xsl:apply-templates mode='toc'/>
</xsl:template>


</xsl:stylesheet>
