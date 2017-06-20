<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : evidence_transform.xsl
    Created on : June 20, 2017, 9:56 PM
    Author     : michal
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="evidence">
        <html>
            <head>
                <title>evidence_transform.xsl</title>
            </head>
            <body>
                <h1>years</h1>
                <ul>
                    <xsl:apply-templates select="year"/>
                </ul>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="year">
        <li>
            <h2><xsl:value-of select="./@yid"/></h2>
            <ul>
                <xsl:apply-templates select="payment"/>
            </ul>
        </li>
    </xsl:template>
    
    <xsl:template match="payment">
        <li>
            <xsl:value-of select="./type"/>&#xA0;
            <xsl:value-of select="./date"/>&#xA0;
            <xsl:value-of select="./info"/>&#xA0;
            <xsl:value-of select="./ammount"/>&#xA0;
        </li>   
    </xsl:template>
    
    
</xsl:stylesheet>
