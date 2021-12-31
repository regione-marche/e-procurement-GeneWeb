<%
/*
 * Created on 23-mar-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA
 // DI LISTA RICERCHE BASE CONTENENTE LA SEZIONE RELATIVA AI TAB
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="listaTabSelezionabili"
	value="${fn:join(gestoreTab.tabSelezionabili,'#')}#" />

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<table class="contenitore-tabs">
	<tr>
		<td>
      <elda:tab contextPath="${pageContext.request.contextPath}" tabindex="2000">
				<elda:voceTab descrizione="Dati Generali" attivo='${gestoreTab.tabAttivo eq "Dati Generali"}' selezionabile='${fn:contains(listaTabSelezionabili, "Dati Generali#")}' href="cambiaTab('DG')"/>
			<c:if test='${applicationScope.gruppiDisabilitati ne "1"}' >
				<elda:voceTab descrizione="Gruppi" attivo='${gestoreTab.tabAttivo eq "Gruppi"}' selezionabile='${fn:contains(listaTabSelezionabili, "Gruppi#")}' href="cambiaTab('GRP')"/>
			</c:if>
				<elda:voceTab descrizione="Sql"  attivo='${gestoreTab.tabAttivo eq "Sql"}' selezionabile='${fn:contains(listaTabSelezionabili, "Campi#")}' href="cambiaTab('SQL')"/>
				<elda:voceTab descrizione="Parametri"  attivo='${gestoreTab.tabAttivo eq "Parametri"}' selezionabile='${fn:contains(listaTabSelezionabili, "Filtri#")}' href="cambiaTab('PAR')"/>
			</elda:tab>
		</td>
	</tr>
</table>