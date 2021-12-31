<%
/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA
 // DI DETTAGLIO PROFILO CONTENENTE LA SEZIONE RELATIVA AI TAB
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="listaTabSelezionabili"
	value="${fn:join(gestoreTab.tabSelezionabili,'#')}#" />

<table class="contenitore-tabs">
	<tr>
		<td>
			<elda:tab contextPath="${pageContext.request.contextPath}" tabindex="2000">
				<elda:voceTab descrizione="Dettaglio" attivo='${gestoreTab.tabAttivo eq "Dettaglio"}' selezionabile='${fn:contains(listaTabSelezionabili, "Dettaglio#")}' href="dettaglioProfilo('${idOggetto}')"/>
				<elda:voceTab descrizione="Utenti" attivo='${gestoreTab.tabAttivo eq "Utenti"}' selezionabile='${fn:contains(listaTabSelezionabili, "Utenti#")}' href="listaAccountProfilo('${idOggetto}')"/>
			<c:if test='${applicationScope.gruppiDisabilitati ne "1"}' >
				<elda:voceTab descrizione="Gruppi" attivo='${gestoreTab.tabAttivo eq "Gruppi"}' selezionabile='${fn:contains(listaTabSelezionabili, "Gruppi#")}' href="listaGruppiProfilo('${idOggetto}')"/>
			</c:if>
			</elda:tab>
		</td>
	</tr>
</table>