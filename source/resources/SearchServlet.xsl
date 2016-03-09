<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Request">

<html>

<head>
	<title>Search the Database</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/SearchServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSPage.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/SearchServlet.js">;</script>
</head>

<body>
<center>

	<h1>Search</h1>
	<p><input type="button" value="Clear" onclick="clearForm()"/></p>

	<form method="POST" accept-charset="UTF-8">
	
	<table id="FormTable">
		<tr>
			<td>Earliest Date:</td>
			<td><input id="earliestDate" class="text" type="text" name="earliestDate" value="{SC/@earliestDate}"/></td>
		</tr>
		
		<tr>
			<td>Latest Date:</td>
			<td><input class="text" type="text" name="latestDate" value="{SC/@latestDate}"/></td>
		</tr>
		
		<tr>
			<td>Route:</td>
			<td><input class="text" type="text" name="route" value="{SC/@route}"/></td>
		</tr>
		
		<tr>
			<td>A/C ID:</td>
			<td><input class="text" type="text" name="acid" value="{SC/@acid}"/></td>
		</tr>
		
		<tr>
			<td>Model:</td>
			<td><input class="text" type="text" name="model" value="{SC/@model}"/></td>
		</tr>
		
		<tr>
			<td>Notes:</td>
			<td><input class="text" type="text" name="notes" value="{SC/@notes}"/></td>
		</tr>
		
		<tr>
			<td>Class:</td>
			<td>
				<input id="asel" type="checkbox" name="asel" value="yes">
					<xsl:if test="SC/@asel">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<label for="asel">ASEL</label>
				</input>
				<br/>
				<input id="ases" type="checkbox" name="ases" value="yes">
					<xsl:if test="SC/@ases">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<label for="ases">ASES</label>
				</input>
				<br/>
				<input id="amel" type="checkbox" name="amel" value="yes">
					<xsl:if test="SC/@amel">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<label for="amel">AMEL</label>
				</input>
				<br/>
				<input id="ames" type="checkbox" name="ames" value="yes">
					<xsl:if test="SC/@ames">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<label for="ames">AMES</label>
				</input>
				<br/>
				<input id="glider" type="checkbox" name="glider" value="yes">
					<xsl:if test="SC/@glider">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<label for="glider">Glider</label> 
				</input>
				<br/>
				<input id="helicopter" type="checkbox" name="helicopter" value="yes">
					<xsl:if test="SC/@helicopter">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<label for="helicopter">Helicopter</label>
				</input>
			</td>
		</tr>
		
		<tr>
			<td><label for="tailwheel">Tailwheel</label>:</td>
			<td>
				<input id="tailwheel" type="checkbox" name="tailwheel" value="yes">
					<xsl:if test="SC/@tailwheel">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
				</input>
			</td>
		</tr>
		<tr>
			<td><label for="retractable">Retractable</label>:</td>
			<td>
				<input id="retractable" type="checkbox" name="retractable" value="yes">
					<xsl:if test="SC/@retractable">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
				</input>
			</td>
		</tr>
		<tr>
			<td><label for="complex">Complex</label>:</td>
			<td>
				<input id="complex" type="checkbox" name="complex" value="yes">
					<xsl:if test="SC/@complex">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
				</input>
			</td>
		</tr>

	</table>

	<p><input type="submit" value="Search"/></p>
	</form>
	
	<table>
		<tr>
			<td>Route:</td>
			<td># = round trip<br/>! = cross country</td>
		</tr>
		<tr>
			<td>Notes:</td>
			<td>* = contains notes<br/># = contains route</td>
		</tr>
	</table>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>