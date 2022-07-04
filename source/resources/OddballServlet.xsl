<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Oddballs">

<html>

<head>
	<title><xsl:value-of select="@title"/></title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/ListFlightsServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSAJAX.js">;</script>
</head>

<body>
<center>
	
	<h1><xsl:value-of select="@title"/></h1>
	<xsl:if test="Flight">
		<table>
			<xsl:call-template name="ColumnHeadings"/>
			<xsl:apply-templates select="Flight"/>
		</table>
	</xsl:if>
	
	<xsl:apply-templates select="OddRoutes"/>
	
	<xsl:if test="MissingAirport">
		<h2>Missing Airports</h2>
		<table>
			<xsl:apply-templates select="MissingAirport"/>
		</table>
	</xsl:if>
	
	<xsl:apply-templates select="UnreferencedAC"/>
	
	<xsl:if test="MissingAC">
		<h1>Missing Aircraft</h1>
		<table>
			<xsl:for-each select="MissingAC">
				<xsl:sort select="@acid"/>
				<tr>
					<td>
						<a href="/addaircraft?acid={@acid}">
							<xsl:value-of select="@acid"/>
						</a>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:if>
	<br/><br/><br/><br/>
</center>
</body>

</html>

</xsl:template>
	
<xsl:template name="ColumnHeadings">
	<tr>
		<th/>
		<th>ID</th>
		<th>Date</th>
		<th>A/C ID</th>
		<th>Route</th>
		<th>Total</th>
		<th>Ldg</th>
		<th>App</th>
		<th>Day</th>
		<th>Night</th>
		<th>XC</th>
		<th>Inst</th>
		<th>Hood</th>
		<th>Dual</th>
		<th>PIC</th>
	</tr>
</xsl:template>

<xsl:template match="Flight">
	<tr>
		<td class="number">
			<xsl:if test="@notes">
				<xsl:attribute name="title"><xsl:value-of select="@notes"/></xsl:attribute>
				<b><xsl:value-of select="position()"/></b>
			</xsl:if>
			<xsl:if test="not(@notes)">
				<xsl:value-of select="position()"/>
			</xsl:if>
		</td>
		<td>
			<a href="/addflight?id={@id}">
				<xsl:value-of select="@id"/>
			</a>
		</td>
		<td>
			<a href="/addflight?id={@id}">
				<xsl:value-of select="substring(@date,1,10)"/>
				<xsl:if test="string-length(@date)&gt;10">
					<span class="index">
						<xsl:value-of select="substring(@date,11)"/>
					</span>
				</xsl:if>
			</a>
		</td>
		<td><a href="/addaircraft?acid={@acid}"><xsl:value-of select="@acid"/></a></td>
		<td>
			<a href="/airports?id={@id}" target="Route">
				<xsl:value-of select="@route"/>
			</a>
		</td>
		<td class="right">
			<xsl:if test="@tach">
				<xsl:attribute name="title">tach=<xsl:value-of select="@tach"/></xsl:attribute>
			</xsl:if>
			<xsl:value-of select="@total"/>
		</td>
		<td class="right"><xsl:value-of select="@ldg"/></td>
		<td class="right"><xsl:value-of select="@app"/></td>
		<td class="right"><xsl:value-of select="@tday"/></td>
		<td class="right"><xsl:value-of select="@tnt"/></td>
		<td class="right"><xsl:value-of select="@txc"/></td>
		<td class="right"><xsl:value-of select="@inst"/></td>
		<td class="right"><xsl:value-of select="@hood"/></td>
		<td class="right"><xsl:value-of select="@dual"/></td>
		<td class="right"><xsl:value-of select="@pic"/></td>
	</tr>
</xsl:template>

<xsl:template match="OddRoutes">
	<h2>Odd Routes</h2>
	<table>
		<xsl:call-template name="ColumnHeadings"/>
		<xsl:apply-templates select="Flight"/>
	</table>
</xsl:template>

<xsl:template match="MissingAirport">
	<tr>
		<td><xsl:value-of select="@id"/></td>
	</tr>
</xsl:template>

<xsl:template match="UnreferencedAC">
	<h2>Unreferenced Aircraft</h2>
	<table>
		<tr>
			<th>A/C ID</th>
			<th>Model</th>
			<th>Category</th>
			<th>Tailwheel</th>
			<th>Retractable</th>
			<th>Complex</th>
		</tr>
		<xsl:for-each select="Aircraft">
			<xsl:sort select="@category"/>
			<xsl:sort select="@model"/>
			<xsl:sort select="@acid"/>
			<tr>
				<td><a href="/addaircraft?acid={@acid}"><xsl:value-of select="@acid"/></a></td>
				<td><xsl:value-of select="@model"/></td>
				<td class="center"><xsl:value-of select="@category"/></td>
				<td class="center">
					<xsl:if test="@tailwheel='yes'">&#10003;</xsl:if>
				</td>
				<td class="center">
					<xsl:if test="@retractable='yes'">&#10003;</xsl:if>
				</td>
				<td class="center">
					<xsl:if test="@complex='yes'">&#10003;</xsl:if>
				</td>
				<td>
					<input type="button" value="Remove" onclick="window.open('?removeAC={@acid}','_self');"/>
				</td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

</xsl:stylesheet>