<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:template match="/Request">

<html>

<head>
	<title>Add Flight</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/AddFlightServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSAJAX.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSPage.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/AddFlightServlet.js">;</script>
	<script>
		function setFocus() {
			document.getElementById("date").focus();
		}
		window.onload = setFocus;
	</script>
</head>

<body>
<center>

	<xsl:if test="Flight/@id != ''">
		<h1>Edit Flight (<xsl:value-of select="Flight/@id"/>)</h1>
	</xsl:if>
	<xsl:if test="Flight/@id = ''">
		<h1>Add Flight</h1>
	</xsl:if>	
	<p><input type="button" value="Today" onclick="today()"/></p>

	<form id="addFlight" method="POST" accept-charset="UTF-8" action="/addflight">
	
	<input type="hidden" id="id" name="id" value="{Flight/@id}"/>

	<table>

		<tr>
			<td>Date:</td>
			<td><input id="date" class="text" type="text" name="date" value="{Flight/@date}" tabindex="1"/></td>
			<td/>
			<td/>
		</tr>
		<tr>
			<td>Aircraft ID:</td>
			<td><input class="text" type="text" name="acid" value="{Flight/@acid}" tabindex="2"/></td>
			<td>XC:</td>
			<td><input class="text" type="text" name="txc" value="{Flight/@txc}" tabindex="9"/></td>
		</tr>
		<tr>
			<td>From:</td>
			<td><input class="text" type="text" name="from" value="{Flight/@from}" tabindex="3"/></td>
			<td>Day:</td>
			<td><input class="text" type="text" name="tday" value="{Flight/@tday}" tabindex="10"/></td>
		</tr>
		<tr>
			<td>To:</td>
			<td><input class="text" type="text" name="to" value="{Flight/@to}" tabindex="4"/></td>
			<td>Night:</td>
			<td><input class="text" type="text" name="tnt" value="{Flight/@tnt}" tabindex="11"/></td>
		</tr>
		<tr>
			<td>Landings:</td>
			<td><input class="text" type="text" name="ldg" value="{Flight/@ldg}" tabindex="5"/></td>
			<td>Approaches:</td>
			<td><input class="text" type="text" name="app" value="{Flight/@app}" tabindex="12"/></td>
		</tr>
		<tr>
			<td>Total Time:</td>
			<td><input class="text" type="text" name="total" value="{Flight/@total}" tabindex="6"/></td>
			<td>Instrument:</td>
			<td><input class="text" type="text" name="inst" value="{Flight/@inst}" tabindex="13"/></td>
		</tr>
		<tr>
			<td>Tach Time:</td>
			<td><input class="text" type="text" name="tach" value="{Flight/@tach}" tabindex="7"/></td>
			<td>Hood:</td>
			<td><input class="text" type="text" name="hood" value="{Flight/@hood}" tabindex="14"/></td>
		</tr>
		<tr>
			<td>PIC:</td>
			<td><input class="text" type="text" name="pic" value="{Flight/@pic}" tabindex="8"/></td>
			<td>Dual:</td>
			<td><input class="text" type="text" name="dual" value="{Flight/@dual}" tabindex="15"/></td>
		</tr>
		<tr>
			<td colspan="4">
				Notes:<br/>
				<textarea name="notes" tabindex="16">
					<xsl:if test="Flight/@notes"><xsl:value-of select="Flight/@notes"/></xsl:if>
					<xsl:if test="not(Flight/@notes)">&#160;</xsl:if>
				</textarea>

			</td>
		</tr>
	</table>

	<p><input type="submit" value="Submit Flight" tabindex="17"/></p>
	<p><input type="button" value="Repeat Previous Search" tabindex="18" onclick="window.open('/search?repeat','_self');"/></p>
	</form>

</center>
</body>

</html>

</xsl:template>

</xsl:stylesheet>