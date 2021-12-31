
<%
	/*
	 * Created on 15-lug-2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="scheda-template.jsp">

	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	<c:set var="codimp" value="${param.codimp}" />
	
	<gene:setString name="titoloMaschera"  value="Art.80 - richiedi verifica per l'operatore economico"/>
	
	<gene:redefineInsert name="head" >
	
		<style type="text/css">
			TABLE.griglia80 {
				margin: 0;
				margin-top: 5px;
				margin-bottom: 5px;
				padding: 0px;
				width: 100%;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}
			
			TABLE.griglia80 TR {
				background-color: #FFFFFF;
			}
			
			TABLE.griglia80 TR.intestazione {
				background-color: #EFEFEF;
			}
			
			TABLE.griglia80 TR.intestazione TD {
				padding: 4 2 4 2;
				text-align: center;
				border: 1px solid #A0AABA;	
				height: 25px;
				font-weight: bold;
			}
					
			TABLE.griglia80 TR TD {
				padding-left: 10px;
				padding-right: 10px;
				padding-top: 2px;
				padding-bottom: 2px;
				height: 20px;
				text-align: left;
				border: 1px solid #A0AABA;
			}


			
		</style>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
	
		<table class="lista">
			<tr> 
				<td colspan="2">
					<table class="griglia80" padding="10 0 10 0;">
						<tr class="intestazione">
							<td>Codice dell'anagrafico</td>
							<td>Esito dell'operazione</td>
							<td>Messaggio</td>
						</tr>
						<c:forEach items="${resultCreaOE}" var="resultCreaOERiga" varStatus="hMapStatus">
							<tr>
								<td style="text-align: center;">${resultCreaOERiga[0]}</td>
								<td style="text-align: center;">${resultCreaOERiga[1]}</td>
								<td style="text-align: center;">${resultCreaOERiga[2]}</td>
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
		
	</gene:redefineInsert>
	
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	<gene:redefineInsert name="addToAzioni"></gene:redefineInsert>
	
</gene:template>



