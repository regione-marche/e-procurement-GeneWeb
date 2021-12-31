<%
  /*
			 * Created on 09/04/2018
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<gene:template file="scheda-template.jsp">
	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	<c:set var="codimp" value="${param.codimp}" />

	<gene:setString name="titoloMaschera" value="Art.80 - richiedi verifica per l'operatore economico" />

	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/Art80CreaOE.do" method="post" name="formArt80CreaOE" >
			<input type="hidden" name="codimp" value="${codimp}" />
			<table class="dettaglio-notab">
				<tr>
					<td colspan="2" style="padding: 0 5 0 5;">
						<br>
						<br>
						Invio dei dati per la creazione di un nuovo operatore economico ai fini del controllo documenti secondo le disposizioni dell'art. 80.
						<br>
						<br>
						<br>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Invia dati" title="Invia dati" onclick="javascript:inviadati();">
						<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
	<gene:redefineInsert name="addToAzioni" >
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:inviadati();" title="Invia dati" tabindex="1503">Invia dati</a>
			</td>
		</tr>
	</gene:redefineInsert>

	<gene:javaScript>

		function inviadati(){
			bloccaRichiesteServer();
			setTimeout("document.formArt80CreaOE.submit()", 250);
		}

	</gene:javaScript>
</gene:template>


