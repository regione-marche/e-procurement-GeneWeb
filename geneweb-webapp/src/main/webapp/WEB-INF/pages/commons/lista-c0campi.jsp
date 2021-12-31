
<%
	/*
	 * Created on 20-Ott-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
		<script type="text/javascript" src="${contextPath}/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.c0campi.js"></script>
		
		<style type="text/css">
		
			TABLE.scheda {
				margin-top: 5px;
				margin-bottom: 5px;
				padding: 0px;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}
	
			TABLE.scheda TR.intestazione {
				background-color: #EFEFEF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.scheda TR.intestazione TD, TABLE.scheda TR.intestazione TH {
				padding: 5 2 5 2;
				text-align: center;
				font-weight: bold;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 30px;
			}
		
			TABLE.scheda TR.sezione {
				background-color: #EFEFEF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.scheda TR.sezione TD, TABLE.scheda TR.sezione TH {
				padding: 5 2 5 2;
				text-align: left;
				font-weight: bold;
				height: 25px;
			}
		
			TABLE.scheda TR {
				background-color: #FFFFFF;
			}
	
			TABLE.scheda TR TD {
				padding-left: 3px;
				padding-top: 1px;
				padding-bottom: 1px;
				padding-right: 3px;
				text-align: left;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 22px;
				font: 11px Verdana, Arial, Helvetica, sans-serif;
			}
			
			TABLE.scheda TR.intestazione TH.ck, TABLE.scheda TR TD.ck {
				width: 22px;
				text-align: center;
			}
			
			img.img_titolo {
				padding-left: 8px;
				padding-right: 8px;
				width: 24px;
				height: 24px;
				vertical-align: middle;
			}
			
			.dataTables_length, .dataTables_filter {
				padding-bottom: 5px;
			}
				
			div.tooltip {
				width: 300px;
				margin-top: 3px;
				margin-bottom:3px;
				border: 1px solid #A0AABA;
				padding: 10px;
				display: none;
				position: absolute;
				z-index: 1000;
				background-color: #F4F4F4;
			}

				
		</style>
		
	</gene:redefineInsert>

	<gene:insert name="addHistory">
		<gene:historyAdd titolo="Lista mnemonici (C0CAMPI)" id="listaC0CAMPI" />
	</gene:insert>	

	<gene:setString name="titoloMaschera" value="Lista mnemonici (C0CAMPI)" />
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<tr>
				<td>
					<div id="tabellaC0CAMPIcontainer" style="margin-left:8px; width: 98%"></div>
					<br>
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert> 
	<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>

</gene:template>

