<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" />

<xsl:preserve-space elements="*"/>

<xsl:param name="mobile"/>

<xsl:template match="/Request">

<html>

<head>
	<meta name="keywords" content="Flight Log "/>
	<title>Flight Log</title>
	<link rel="stylesheet" href="/BaseStyles.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPage.css" type="text/css"/>
	<link rel="stylesheet" href="/JSPopup.css" type="text/css"/>
	<link rel="stylesheet" href="/FlightLogServlet.css" type="text/css"/>
	<script language="JavaScript" type="text/javascript" src="/JSUtil.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSAJAX.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSUser.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSPopup.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSLoginPopup.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/JSPage.js">;</script>
	<script language="JavaScript" type="text/javascript" src="/FlightLogServlet.js">;</script>
</head>

<body>

	<div class="header">
		<div class="headerLeft">
			<span class="name">Flight Log</span>
		</div>
		<div class="headerRight">
			<div style="float:right; margin-top:7px;">
				<xsl:text>&#160;&#160;</xsl:text>
				<span class="headerText">Welcome </span>
				<span class="headerText" id="usernameSpan"><xsl:value-of select="@username"/></span>
				<xsl:text>&#160;&#160;</xsl:text>
				<span class="headerText">
					<xsl:text>|&#160;&#160;</xsl:text>
					<xsl:if test="@authenticated='yes'">
						<a href="javascript:openURL('/login?logout=true&amp;url=/flightlog', '_self');">Logout</a>
					</xsl:if>
					<xsl:if test="not(@authenticated='yes')">
						<a href="javascript:showLoginPopup('/flightlog');">Login</a>
					</xsl:if>
					<xsl:text>&#160;&#160;</xsl:text>
				</span>
			</div>
		</div>
	</div>

	<div id="main" class="main">
		<div id="left" class="left">
			<xsl:if test="@authenticated='yes'">
			
				<div class="L1">Search</div>
				<div class="L2"><a href="javascript:loadFrame('/search')">Search flights</a></div>				
				<div class="L2"><a href="javascript:loadFrame('/search?recent')">Recent flights</a></div>
				<div class="L2"><a href="javascript:loadFrame('/oddballs')">Find oddballs</a></div>				
				<div class="L2"><a href="javascript:loadFrame('/listimages')">List images</a></div>				
				<div class="L2"><a href="javascript:loadFrame('/summary?insurance')">Insurance Form</a></div>				
				<div class="L2"><a href="javascript:loadFrame('/summary')">Summary</a></div>				

				<div class="L1">Flights</div>
				<div class="L2"><a href="javascript:loadFrame('/addflight')">Add flight</a></div>
				<div class="L2"><a href="javascript:loadFrame('/listflights')">List flights</a></div>
				<div class="L2"><a href="javascript:loadFrame('/ping')">Date/Time</a></div>

				<div class="L1">Aircraft</div>
				<div class="L2"><a href="javascript:loadFrame('/addaircraft')">Add aircraft</a></div>
				<div class="L2"><a href="javascript:loadFrame('/listaircraft')">List aircraft</a></div>

				<div class="L1">Airports</div>
				<div class="L2"><a href="javascript:loadFrame('/airports')">Search airports</a></div>

				<xsl:if test="@admin='yes'">
					<div class="L1">Admin</div>
					<div class="L2"><a href="javascript:loadFrame('/save')">Save database</a></div>
					<div class="L2"><a href="javascript:loadFrame('/users?suppress')">User Manager</a></div>
					<div class="L2"><a href="javascript:loadFrame('/system?suppress')">System</a></div>
					<div class="L2"><a href="javascript:loadFrame('/environment?suppress')">Environment</a></div>
					<div class="L2"><a href="javascript:loadFrame('/logs?suppress')">Log Viewer</a></div>
					<div class="L2"><a href="javascript:loadFrame('/level?suppress')">Logger Levels</a></div>
					<div class="L2"><a href="javascript:loadFrame('/attacklog?suppress')">Attack Log</a></div>
					<div class="L2"><a href="javascript:loadFrame('/svrsts')">Server Status</a></div>
					<div class="L2"><a href="javascript:loadFrame('/convert')">Convert</a></div>
					<div class="L2"><a href="javascript:loadFrame('/initialize')">Initialize</a></div>
				</xsl:if>
			</xsl:if>
		</div>

		<div id="right" class="right">
			&#160;
		</div>
	</div>

</body>

</html>

</xsl:template>

</xsl:stylesheet>