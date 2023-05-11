<%
/*
 * Created on 27-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA
 // DI LISTA GRUPPI CONTENENTE LA SEZIONE RELATIVA AI TAB
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="listaOpzioniDisponibili"
	value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaTabSelezionabili"
	value="${fn:join(gestoreTab.tabSelezionabili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" 
	value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<table class="contenitore-tabs">
	<tr>
		<td>
      <elda:tab contextPath="${pageContext.request.contextPath}" tabindex="2000">
				<elda:voceTab descrizione="Dettaglio" attivo='${gestoreTab.tabAttivo eq "Dettaglio"}' selezionabile='${fn:contains(listaTabSelezionabili, "Dettaglio#")}' href="dettaglioGruppo(${idOggetto})"/>
				<elda:voceTab descrizione="Utenti" attivo='${gestoreTab.tabAttivo eq "Utenti"}' selezionabile='${fn:contains(listaTabSelezionabili, "Utenti#")}' href="listaUtentiGruppo(${idOggetto})"/>
			<!-- F.D. 27/02/2007 cambia la gestione dei menù: vengono abilitati i menù in base alle opzioni utente (ou) -->  
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP2#") && fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
				<elda:voceTab descrizione="Report" attivo='${gestoreTab.tabAttivo eq "Report"}' selezionabile='${fn:contains(listaTabSelezionabili, "Report#")}' href="listaRicercheGruppo(${idOggetto})"/>
			</c:if>
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#") && fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>
				<elda:voceTab descrizione="Modelli"  attivo='${gestoreTab.tabAttivo eq "Modelli"}' selezionabile='${fn:contains(listaTabSelezionabili, "Modelli#")}' href="listaModelliGruppo(${idOggetto})"/>
			</c:if>
			</elda:tab>
		</td>
	</tr>
</table>