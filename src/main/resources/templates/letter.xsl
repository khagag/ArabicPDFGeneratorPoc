<?xml version="1.0" encoding="UTF-8"?>
<!--
  Formal letter template.
  Input fields: date, sender{name,title,company}, recipient{name,address}, subject, paragraphs[]
  Parameter "lang": en | ar   (ar => right-to-left + Arabic salutation/closing)
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:output method="xml" encoding="UTF-8" indent="no"/>

  <xsl:param name="lang" select="'en'"/>
  <xsl:variable name="rtl" select="$lang = 'ar'"/>
  <xsl:variable name="wm">
    <xsl:choose>
      <xsl:when test="$rtl">rl-tb</xsl:when>
      <xsl:otherwise>lr-tb</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:template match="/data">
    <fo:root font-family="Amiri, sans-serif" font-size="13pt" writing-mode="{$wm}" xml:lang="{$lang}">

      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" page-width="210mm" page-height="297mm"
                               margin-top="25mm" margin-bottom="20mm"
                               margin-left="25mm" margin-right="25mm"
                               writing-mode="{$wm}">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="A4">
        <fo:flow flow-name="xsl-region-body">
          <fo:block language="{$lang}" writing-mode="{$wm}" line-height="1.6">

            <!-- letterhead -->
            <xsl:if test="sender/company">
              <fo:block font-size="20pt" font-weight="bold" color="#1F3A5F"
                        border-bottom="1.5pt solid #1F3A5F" padding-bottom="2mm" space-after="8mm">
                <xsl:value-of select="sender/company"/>
              </fo:block>
            </xsl:if>

            <!-- date -->
            <fo:block text-align="end" space-after="8mm"><xsl:value-of select="date"/></fo:block>

            <!-- recipient -->
            <fo:block font-weight="bold"><xsl:value-of select="recipient/name"/></fo:block>
            <fo:block space-after="8mm"><xsl:value-of select="recipient/address"/></fo:block>

            <!-- subject -->
            <xsl:if test="subject">
              <fo:block font-weight="bold" text-decoration="underline" text-align="center" space-after="8mm">
                <xsl:value-of select="subject"/>
              </fo:block>
            </xsl:if>

            <!-- salutation -->
            <fo:block space-after="5mm">
              <xsl:choose>
                <xsl:when test="$rtl">
                  <xsl:text>السيد/ة </xsl:text><xsl:value-of select="recipient/name"/><xsl:text> المحترم/ة،</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>Dear </xsl:text><xsl:value-of select="recipient/name"/><xsl:text>,</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </fo:block>

            <!-- body -->
            <xsl:for-each select="paragraphs/item">
              <fo:block text-align="justify" space-after="4mm"><xsl:value-of select="."/></fo:block>
            </xsl:for-each>

            <!-- closing -->
            <fo:block space-before="8mm" space-after="14mm" keep-with-next.within-page="always">
              <xsl:choose>
                <xsl:when test="$rtl">وتفضلوا بقبول فائق الاحترام والتقدير،</xsl:when>
                <xsl:otherwise>Sincerely,</xsl:otherwise>
              </xsl:choose>
            </fo:block>

            <fo:block font-weight="bold"><xsl:value-of select="sender/name"/></fo:block>
            <fo:block><xsl:value-of select="sender/title"/></fo:block>

          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

</xsl:stylesheet>
