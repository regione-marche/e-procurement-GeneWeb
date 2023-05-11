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

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<!-- id modello: ${idModello} Metodo: <%=request.getParameter("metodo")%> -->
<table class="contenitore-tabs">
	<tr>
		<td>
      <elda:tab contextPath="${pageContext.request.contextPath}" tabindex="2000">
				<elda:voceTab descrizione="Dettaglio" attivo='${gestoreModelliTab.tabAttivo eq "Dettaglio"}' selezionabile='${fn:contains(gestoreModelliTab.listaTabSelezionabili, "#Dettaglio#")}' href="dettaglioModello(${idModello})"/>
				 <!-- F.D. in base alla nuova gestione dei permessi nascondo tab del gruppo
	    		per gli utenti che non hanno l'ou50 -->
				<c:if test='${applicationScope.gruppiDisabilitati ne "1"}' >
					<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>		
						<elda:voceTab descrizione="Gruppi" attivo='${gestoreModelliTab.tabAttivo eq "Gruppi"}' selezionabile='${fn:contains(gestoreModelliTab.listaTabSelezionabili, "#Gruppi#")}' href="listaGruppiModello(${idModello})"/>
					</c:if>
				</c:if>
				<elda:voceTab descrizione="Parametri" attivo='${gestoreModelliTab.tabAttivo eq "Parametri"}' selezionabile='${fn:contains(gestoreModelliTab.listaTabSelezionabili, "#Parametri#")}' href="listaParametriModello(${idModello})"/>
			</elda:tab>
		</td>
	</tr>
</table>