<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Images">
	<html>
	<head>
		<title>Images</title>
		<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
		<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
		<link rel="stylesheet" href="/ListFlightsServlet.css" type="text/css"/>
	<style>
		h1 {text-align:center;}
		p {margin-left:40px; }
	</style>
	</head>
	<body>
		<h1>Images</h1>
		<xsl:apply-templates select="Flight">
			<xsl:sort select="@date" order="descending"/>
		</xsl:apply-templates>
	</body>
	</html>
</xsl:template>

<xsl:template match="Flight">
	<h2>
		<a href="/addflight?id={@id}">
			<xsl:value-of select="@date"/>
		</a>
	</h2>
	<xsl:apply-templates select="Image"/>
</xsl:template>

<xsl:template match="Image">
	<xsl:variable name="name" select="@name"/>
	<xsl:if test="$name != ''">
		<xsl:variable name="lc" select="translate($name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
		<xsl:variable name="ext" select="substring($lc, string-length($lc) - 3)"/>
		<!--<p><xsl:value-of select="$lc"/> - <xsl:value-of select="$ext"/></p>-->
		<xsl:choose>
			<xsl:when test="$ext='.pdf'">
				<p>
					<a href="/images/{$name}" target="_blank" title="{$name}">PDF document</a>
					<xsl:text> </xsl:text>
					<a href="javascript:deleteImage('{Flight/@id}','{$name}');"><img src="/icons/closebox.gif"/></a>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p>
					<img src="/images/{$name}" title="{$name}"/>
					<xsl:text> </xsl:text>
					<a href="javascript:deleteImage('{Flight/@id}','{$name}');"><img src="/icons/closebox.gif"/></a>
				</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>
</xsl:template>
</xsl:stylesheet>