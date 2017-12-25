<?xml version='1.0' encoding='UTF-8'?>

<xsl:stylesheet 
  xmlns="http://efele.net/2010/ns/bml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bml='http://efele.net/2010/ns/bml'
  version="2.0">

<xsl:import href="../../../tools/toefele.xsl"/>

<xsl:param name='endnotes'/>
<xsl:param name='review'/>

<xsl:output 
  method="xml"
  indent="no"
  encoding="UTF-8"/>


<xsl:variable name='endnotetype'>
  <xsl:choose>
    <xsl:when test='$endnotes="no"'>none</xsl:when>
    <xsl:otherwise>v</xsl:otherwise>
  </xsl:choose>
</xsl:variable>


<xsl:key name='note-key' match='bml:note' use='@id'/>

<xsl:key name='enote-key' match='bml:note[@type != $endnotetype]' use='@id'/>


<xsl:template match='bml:toc'>
  <xsl:copy>
    <bml:tocentry idref="page-titre">
        <xsl:attribute name='label'>
          <xsl:value-of select='//bml:bml/bml:metadata/bml:electronique/bml:titre'/>
        </xsl:attribute>

        <xsl:apply-templates select='//bml:bml/bml:page-sequences' mode='toc'/>

    </bml:tocentry>

    <bml:tocentry label="Colophon" idref="colophon"/>
  </xsl:copy>
</xsl:template>

<xsl:template match='bml:page-sequences'>
  <xsl:variable name='metadata' as="node()" select='//bml:bml/bml:metadata'/>
  <xsl:copy>

    <xsl:for-each select='//bml:bml/bml:cover'>
      <bml:page-sequence>
        <bml:div id='couverture'>
          <bml:img id="coverimage" class="fullpageimage"
                   src="{@src}" alt="{@alt}"/>
        </bml:div>
      </bml:page-sequence>
    </xsl:for-each>

    <xsl:call-template name='efelecover'/>

    <xsl:apply-templates select='bml:div | bml:partie | bml:lettre'/>

    <xsl:apply-templates mode='endnotes'/>
    
    <xsl:call-template name='colophon'>
      <xsl:with-param name='metadata' select='$metadata' tunnel="yes"/>
    </xsl:call-template>

  </xsl:copy>
</xsl:template>



<xsl:template match='bml:div[descendant::bml:div]' priority='10'>
  <page-sequence>

    <div>
      <xsl:for-each select='@id'>
        <xsl:copy/>
      </xsl:for-each>

    <xsl:apply-templates select='bml:div[1]/preceding-sibling::*'/>

    </div>
  </page-sequence>

  <xsl:apply-templates select='bml:div[1]'/>

  <xsl:apply-templates select='bml:div[1]/following-sibling::*'/>
</xsl:template>



<xsl:template match='bml:div'>
  <page-sequence>

    <div>
      <xsl:for-each select='@id'>
        <xsl:copy/>
      </xsl:for-each>

    <xsl:apply-templates/>

    <xsl:call-template name='myfootnotes'>
      <xsl:with-param name='parents' select='.'/>
    </xsl:call-template>

    </div>
  </page-sequence>
</xsl:template>


<xsl:template match='bml:partie' mode='endnotes'>
  <xsl:call-template name='doendnotes'>
    <xsl:with-param name='notes' select='bml:livre[1]/preceding-sibling::*/descendant::bml:note[@type=$endnotetype]'/>
  </xsl:call-template>

  <xsl:apply-templates select='bml:livre[1] | bml:livre[1]/following-sibling::*'
                       mode='endnotes'/>
</xsl:template>


<xsl:template match='bml:livre' mode='endnotes'>
  <xsl:call-template name='doendnotes'>
    <xsl:with-param name='notes' select='bml:chapitre[1]/preceding-sibling::*/descendant::bml:note[@type=$endnotetype]'/>
  </xsl:call-template>

  <xsl:apply-templates select='bml:chapitre[1] | bml:chapitre[1]/following-sibling::*'
                       mode='endnotes'/>
</xsl:template>


<xsl:template match='bml:div[descendant::bml:div]' priority='10' mode='endnotes'>
  <xsl:call-template name='doendnotes'>
    <xsl:with-param name='notes' select='bml:div[1]/preceding-sibling::*/descendant::bml:note[@type=$endnotetype]'/>
  </xsl:call-template>

  <xsl:apply-templates select='bml:div[1] | bml:div[1]/following-sibling::*'
                       mode='endnotes'/>
</xsl:template>


<xsl:template match='bml:div | bml:lettre | bml:chapitre' mode='endnotes'>
  <xsl:call-template name='doendnotes'>
    <xsl:with-param name='notes' select='descendant::bml:note[@type=$endnotetype]'/>
  </xsl:call-template>
</xsl:template>



<xsl:template match='*' mode='endnotes'/>

<xsl:template name='doendnotes'>
  <xsl:param name='notes'/>

  <xsl:if test='count($notes) != 0'>
    <page-sequence>
      <xsl:call-template name='dofootnotes'>
        <xsl:with-param name='notes' select='$notes'/>
      </xsl:call-template>
    </page-sequence>
  </xsl:if>
</xsl:template>



<xsl:template match='bml:partie'>
  <page-sequence>
    <div id='partie.{@p}'>
      <xsl:apply-templates
          select='bml:livre[1]/preceding-sibling::*'/>

    </div>
    <vsep class="rule"/>

    <xsl:apply-templates select='bml:livre[1]'/>


    <xsl:call-template name='myfootnotes'>
      <xsl:with-param name='parents'
                      select='bml:livre[1]/preceding-sibling::*
                              | bml:livre[1]/bml:chapitre[1]/preceding-sibling::*
                              | bml:livre[1]/bml:chapitre[1]'/>
    </xsl:call-template>

  </page-sequence>


  <xsl:apply-templates
      select='bml:livre[1]/bml:chapitre[1]/following-sibling::*'/>

  <xsl:apply-templates 
      select='bml:livre[1]/following-sibling::*'/>
</xsl:template>


<xsl:template match='bml:livre[1]'>

  <div id='livre.{@l}'>
    <xsl:apply-templates 
        select='bml:chapitre[1]/preceding-sibling::*'/>
    
  </div>
  <vsep class="rule"/>

  <xsl:apply-templates select='bml:chapitre[1]'/>

</xsl:template>



<xsl:template match='bml:livre'>

  <page-sequence>

    <div id='livre.{@l}'>
      <xsl:apply-templates 
          select='bml:chapitre[1]/preceding-sibling::*'/>
    </div>

    <vsep class="rule"/>

    <xsl:apply-templates select='bml:chapitre[1]'/>

    <xsl:call-template name='myfootnotes'>
      <xsl:with-param name='parents'
                      select='bml:chapitre[1]/preceding-sibling::* 
                            | bml:chapitre[1]'/>
    </xsl:call-template>

  </page-sequence>

  <xsl:apply-templates 
      select='bml:chapitre[1]/following-sibling::*'/>
</xsl:template>



<xsl:template match='bml:chapitre[1]'>
  <div id='chapitre.{@ch}'>
    <xsl:apply-templates/>
  </div>
</xsl:template>


<xsl:template match='bml:chapitre'>
  <page-sequence>
    <div id='chapitre.{@ch}'>
      <xsl:apply-templates/>
    </div>

    <xsl:call-template name='myfootnotes'>
      <xsl:with-param name='parents' select='.'/>
    </xsl:call-template>

  </page-sequence>
</xsl:template>






<xsl:template match='bml:partie/bml:p[1] | bml:chapitre/bml:p[1] | bml:livre/bml:p[1]'>
  <h2><xsl:apply-templates/></h2>
</xsl:template>

<xsl:template match='bml:chapitre/bml:p[2] | bml:livre/bml:p[2]'>
  <h2><s><xsl:apply-templates/></s></h2>
</xsl:template>


<xsl:template match='bml:lettre'>
  <page-sequence>
    <div id='{@id}'>
      <xsl:apply-templates/>
      <xsl:call-template name='myfootnotes'>
        <xsl:with-param name='parents' select='.'/>
      </xsl:call-template>
    </div>
  </page-sequence>
</xsl:template>

<xsl:template match='bml:entete'>
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match='bml:lettre/bml:entete/bml:titre'>
  <h2><xsl:apply-templates/></h2>
</xsl:template>

<xsl:template match='bml:dea | bml:lieu'>
  <h2><s><xsl:apply-templates/></s></h2>
</xsl:template>

<xsl:template match='bml:lune'>
  <vsep class="emptyline"/>
  <p><xsl:apply-templates/></p>
</xsl:template>






<xsl:template match='bml:oa'>
  <xsl:choose>
    <xsl:when test='$review="yes"'><u>a</u></xsl:when>
    <xsl:otherwise>a</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match='bml:OA'>
  <xsl:choose>
    <xsl:when test='$review="yes"'><u>A</u></xsl:when>
    <xsl:otherwise>A</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match='bml:entree'>
  <p class='entree'><xsl:apply-templates/></p>
</xsl:template>









<!-- ______________________________________________ notes ___-->

<xsl:template match='bml:note'/>

<xsl:template name='notenumber'>
  <xsl:choose>
    <xsl:when test='@type = "v"'>
      <xsl:number level='any' format='a' count='bml:note[@type="v"]' from='bml:lettre | bml:div | bml:partie | bml:livre | bml:chapitre'/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:number level='any' count='bml:note[@type!="v"]' from='bml:lettre | bml:div | bml:partie | bml:livre | bml:chapitre'/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<xsl:template name='myfootnotes'>
  <xsl:param name='parents' select='.'/>

  <xsl:variable name='notes' select='$parents/descendant::bml:noteref except $parents/descendant-or-self::bml:note/descendant::bml:noteref'/>
  <xsl:variable name='noteids' select='$notes/@noteid'/>

  <xsl:call-template name='dofootnotes'>
    <xsl:with-param name='notes' select='key("enote-key", $noteids)'/>
  </xsl:call-template>
</xsl:template>


<xsl:template name='dofootnotes'>
  <xsl:param name='notes'/>

  <xsl:if test='$notes'>
    <notes>
      <xsl:for-each select='$notes'>
        <note id='{@id}' type='{if (@type = "m") then "e" else @type}'>
          <xsl:attribute name='label'>
            <xsl:call-template name='notenumber'/>
          </xsl:attribute>

            <xsl:apply-templates/>

            <xsl:call-template name='myfootnotes'>
              <xsl:with-param name='parents' select='*'/>
            </xsl:call-template>

        </note>
      </xsl:for-each>
    </notes>
  </xsl:if>
</xsl:template>










<xsl:template match='bml:lettre' mode='toc'>
  <tocentry label='{bml:entete/bml:titre}' idref='{@id}'>
    <xsl:apply-templates mode='toc'/> 
  </tocentry>
</xsl:template>

<xsl:template match='bml:partie | bml:livre | bml:chapitre' mode='toc'>
  <tocentry label='{./bml:p[1]}' idref='{local-name(.)}.{@p}{@l}{@ch}'>
    <xsl:apply-templates mode='toc'/>
  </tocentry>
</xsl:template>

<xsl:template match='bml:div[@toc]' mode='toc'>
  <tocentry label='{@toc}' idref='{@id}'>
    <xsl:apply-templates mode='toc'/>
  </tocentry>
</xsl:template>

<xsl:template match='node()' mode='toc'>
  <xsl:apply-templates mode='toc'/>
</xsl:template>


</xsl:stylesheet>
