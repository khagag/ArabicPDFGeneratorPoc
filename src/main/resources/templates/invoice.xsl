<?xml version="1.0" encoding="UTF-8"?>
<!--
  Invoice template.  Input: <data> built from the JSON "data" object.
  Parameter "lang": en | ar   (ar => right-to-left + Arabic labels)
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

  <xsl:attribute-set name="cell">
    <xsl:attribute name="border">0.5pt solid #CCCCCC</xsl:attribute>
    <xsl:attribute name="padding">5pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="head-cell" use-attribute-sets="cell">
    <xsl:attribute name="background-color">#1F3A5F</xsl:attribute>
    <xsl:attribute name="color">#FFFFFF</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <!-- ===== helpers ===== -->
  <xsl:template name="label">
    <xsl:param name="en"/>
    <xsl:param name="ar"/>
    <xsl:choose>
      <xsl:when test="$rtl"><xsl:value-of select="$ar"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$en"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- XSLT 1.0 has no sum(a*b), so accumulate recursively -->
  <xsl:template name="sum-lines">
    <xsl:param name="nodes"/>
    <xsl:param name="acc" select="0"/>
    <xsl:choose>
      <xsl:when test="$nodes">
        <xsl:call-template name="sum-lines">
          <xsl:with-param name="nodes" select="$nodes[position() &gt; 1]"/>
          <xsl:with-param name="acc"
                          select="$acc + number($nodes[1]/quantity) * number($nodes[1]/unitPrice)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$acc"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- a "Label: value" row -->
  <xsl:template name="meta-row">
    <xsl:param name="en"/>
    <xsl:param name="ar"/>
    <xsl:param name="value"/>
    <xsl:if test="string($value)">
      <fo:table-row>
        <fo:table-cell padding="2pt">
          <fo:block font-weight="bold">
            <xsl:call-template name="label">
              <xsl:with-param name="en" select="$en"/>
              <xsl:with-param name="ar" select="$ar"/>
            </xsl:call-template>
          </fo:block>
        </fo:table-cell>
        <fo:table-cell padding="2pt">
          <fo:block><xsl:value-of select="$value"/></fo:block>
        </fo:table-cell>
      </fo:table-row>
    </xsl:if>
  </xsl:template>

  <!-- ===== document ===== -->
  <xsl:template match="/data">

    <xsl:variable name="subtotalRtf">
      <xsl:call-template name="sum-lines">
        <xsl:with-param name="nodes" select="items/item"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="subtotal" select="number($subtotalRtf)"/>
    <xsl:variable name="rate">
      <xsl:choose>
        <xsl:when test="number(taxRate) &gt;= 0"><xsl:value-of select="number(taxRate)"/></xsl:when>
        <xsl:otherwise>0</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="tax" select="$subtotal * number($rate) div 100"/>
    <xsl:variable name="total" select="$subtotal + $tax"/>
    <xsl:variable name="cur" select="string(currency)"/>

    <fo:root font-family="Amiri, sans-serif" font-size="11pt" writing-mode="{$wm}" xml:lang="{$lang}">

      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" page-width="210mm" page-height="297mm"
                               margin-top="15mm" margin-bottom="12mm"
                               margin-left="20mm" margin-right="20mm"
                               writing-mode="{$wm}">
          <fo:region-body margin-bottom="12mm"/>
          <fo:region-after extent="10mm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="A4">

        <fo:static-content flow-name="xsl-region-after">
          <fo:block text-align="center" font-size="9pt" color="#777777" language="{$lang}">
            <xsl:call-template name="label">
              <xsl:with-param name="en" select="'Page'"/>
              <xsl:with-param name="ar" select="'صفحة'"/>
            </xsl:call-template>
            <xsl:text> </xsl:text><fo:page-number/><xsl:text> / </xsl:text>
            <fo:page-number-citation-last ref-id="end-of-document"/>
          </fo:block>
        </fo:static-content>

        <fo:flow flow-name="xsl-region-body">
          <fo:block language="{$lang}" writing-mode="{$wm}">

            <!-- title -->
            <fo:block font-size="26pt" font-weight="bold" color="#1F3A5F" space-after="6mm"
                      border-bottom="2pt solid #1F3A5F" padding-bottom="2mm">
              <xsl:call-template name="label">
                <xsl:with-param name="en" select="'INVOICE'"/>
                <xsl:with-param name="ar" select="'فاتورة'"/>
              </xsl:call-template>
            </fo:block>

            <!-- company | invoice meta -->
            <fo:table table-layout="fixed" width="100%" space-after="6mm" writing-mode="{$wm}">
              <fo:table-column column-width="proportional-column-width(1)"/>
              <fo:table-column column-width="proportional-column-width(1)"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block font-weight="bold" font-size="13pt"><xsl:value-of select="company/name"/></fo:block>
                    <fo:block><xsl:value-of select="company/address"/></fo:block>
                    <xsl:if test="company/taxId">
                      <fo:block>
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Tax ID: '"/>
                          <xsl:with-param name="ar" select="'الرقم الضريبي: '"/>
                        </xsl:call-template>
                        <xsl:value-of select="company/taxId"/>
                      </fo:block>
                    </xsl:if>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:table table-layout="fixed" width="100%" writing-mode="{$wm}">
                      <fo:table-column column-width="proportional-column-width(1)"/>
                      <fo:table-column column-width="proportional-column-width(1)"/>
                      <fo:table-body>
                        <xsl:call-template name="meta-row">
                          <xsl:with-param name="en" select="'Invoice No.'"/>
                          <xsl:with-param name="ar" select="'رقم الفاتورة'"/>
                          <xsl:with-param name="value" select="string(invoiceNumber)"/>
                        </xsl:call-template>
                        <xsl:call-template name="meta-row">
                          <xsl:with-param name="en" select="'Date'"/>
                          <xsl:with-param name="ar" select="'التاريخ'"/>
                          <xsl:with-param name="value" select="string(date)"/>
                        </xsl:call-template>
                        <xsl:call-template name="meta-row">
                          <xsl:with-param name="en" select="'Due date'"/>
                          <xsl:with-param name="ar" select="'تاريخ الاستحقاق'"/>
                          <xsl:with-param name="value" select="string(dueDate)"/>
                        </xsl:call-template>
                      </fo:table-body>
                    </fo:table>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>

            <!-- bill to -->
            <fo:block space-after="6mm" background-color="#F2F5F9" padding="4pt">
              <fo:block font-weight="bold" color="#1F3A5F">
                <xsl:call-template name="label">
                  <xsl:with-param name="en" select="'Bill to'"/>
                  <xsl:with-param name="ar" select="'العميل'"/>
                </xsl:call-template>
              </fo:block>
              <fo:block font-weight="bold"><xsl:value-of select="customer/name"/></fo:block>
              <fo:block><xsl:value-of select="customer/address"/></fo:block>
              <fo:block><xsl:value-of select="customer/phone"/></fo:block>
            </fo:block>

            <!-- items -->
            <xsl:if test="items/item">
              <fo:table table-layout="fixed" width="100%" space-after="4mm" writing-mode="{$wm}">
                <fo:table-column column-width="proportional-column-width(5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-header>
                  <fo:table-row>
                    <fo:table-cell xsl:use-attribute-sets="head-cell">
                      <fo:block text-align="start">
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Description'"/>
                          <xsl:with-param name="ar" select="'الوصف'"/>
                        </xsl:call-template>
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="head-cell">
                      <fo:block text-align="center">
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Qty'"/>
                          <xsl:with-param name="ar" select="'الكمية'"/>
                        </xsl:call-template>
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="head-cell">
                      <fo:block text-align="end">
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Unit price'"/>
                          <xsl:with-param name="ar" select="'سعر الوحدة'"/>
                        </xsl:call-template>
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="head-cell">
                      <fo:block text-align="end">
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Total'"/>
                          <xsl:with-param name="ar" select="'الإجمالي'"/>
                        </xsl:call-template>
                      </fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                  <xsl:for-each select="items/item">
                    <fo:table-row>
                      <fo:table-cell xsl:use-attribute-sets="cell">
                        <fo:block text-align="start"><xsl:value-of select="description"/></fo:block>
                      </fo:table-cell>
                      <fo:table-cell xsl:use-attribute-sets="cell">
                        <fo:block text-align="center"><xsl:value-of select="quantity"/></fo:block>
                      </fo:table-cell>
                      <fo:table-cell xsl:use-attribute-sets="cell">
                        <fo:block text-align="end"><xsl:value-of select="format-number(number(unitPrice), '#,##0.00')"/></fo:block>
                      </fo:table-cell>
                      <fo:table-cell xsl:use-attribute-sets="cell">
                        <fo:block text-align="end"><xsl:value-of select="format-number(number(quantity) * number(unitPrice), '#,##0.00')"/></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                  </xsl:for-each>
                </fo:table-body>
              </fo:table>

              <!-- totals -->
              <fo:table table-layout="fixed" width="100%" space-after="6mm" writing-mode="{$wm}">
                <fo:table-column column-width="proportional-column-width(5)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-body>
                  <fo:table-row>
                    <fo:table-cell><fo:block/></fo:table-cell>
                    <fo:table-cell padding="3pt">
                      <fo:block text-align="start">
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Subtotal'"/>
                          <xsl:with-param name="ar" select="'المجموع الفرعي'"/>
                        </xsl:call-template>
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="3pt">
                      <fo:block text-align="end"><xsl:value-of select="format-number($subtotal, '#,##0.00')"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row>
                    <fo:table-cell><fo:block/></fo:table-cell>
                    <fo:table-cell padding="3pt">
                      <fo:block text-align="start">
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Tax'"/>
                          <xsl:with-param name="ar" select="'الضريبة'"/>
                        </xsl:call-template>
                        <xsl:text> (</xsl:text><xsl:value-of select="number($rate)"/><xsl:text>%)</xsl:text>
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="3pt">
                      <fo:block text-align="end"><xsl:value-of select="format-number($tax, '#,##0.00')"/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row font-weight="bold" font-size="13pt">
                    <fo:table-cell><fo:block/></fo:table-cell>
                    <fo:table-cell padding="3pt" border-top="1.5pt solid #1F3A5F">
                      <fo:block text-align="start">
                        <xsl:call-template name="label">
                          <xsl:with-param name="en" select="'Total'"/>
                          <xsl:with-param name="ar" select="'الإجمالي'"/>
                        </xsl:call-template>
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="3pt" border-top="1.5pt solid #1F3A5F">
                      <fo:block text-align="end">
                        <xsl:value-of select="format-number($total, '#,##0.00')"/>
                        <xsl:if test="$cur != ''"><xsl:text> </xsl:text><xsl:value-of select="$cur"/></xsl:if>
                      </fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>
            </xsl:if>

            <!-- notes -->
            <xsl:if test="notes">
              <fo:block font-size="10pt" color="#444444" text-align="start">
                <fo:block font-weight="bold">
                  <xsl:call-template name="label">
                    <xsl:with-param name="en" select="'Notes'"/>
                    <xsl:with-param name="ar" select="'ملاحظات'"/>
                  </xsl:call-template>
                </fo:block>
                <fo:block><xsl:value-of select="notes"/></fo:block>
              </fo:block>
            </xsl:if>

            <fo:block id="end-of-document"/>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

</xsl:stylesheet>
