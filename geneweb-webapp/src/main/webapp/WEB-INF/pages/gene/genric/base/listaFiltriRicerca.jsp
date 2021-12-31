<%
/*
 * Created on 29-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // ARGOMENTI CONTENENTE LA EFFETTIVA LISTA DEI FILTRI DI UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<fmt:setBundle basename="AliceResources"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="testata" value="${sessionScope.recordDettRicerca.testata}" scope="request" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<html:form action="/ListaFiltriRicercaBase">
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table name="${elencoFiltri}" class="datilista" id="filtro">
					<display:column title="Opzioni" style="width:50px">
						<elda:linkPopupRecord idRecord="${filtro.progressivo}" contextPath="${contextPath}" />
						<input type="checkbox" name="id" value="${filtro.progressivo}" />
					</display:column>
					<display:column title="Campo">${fn:substringAfter(filtro.mnemonicoCampo, "_")}</display:column>
					<display:column property="descrizioneOperatore" title="Operatore">  </display:column>
					<display:column property="valoreConfronto" title="Valore confronto" decorator="it.eldasoft.gene.commons.web.displaytag.SpaziHTMLDecorator">  </display:column>
				</display:table>
			</td>
		</tr>
<% /*
		La spunta sul filtro livello utente non e' visibile ne tanto meno gestibile
		dagli utenti che possono creare solo report base personali: per questi utenti
		la gestione del filtro livello utente avviene in maniera automatica
		
		Spiegazione del test seguente:
		  ou49 = solo report personali
		  ou53 = report base
		  ou54 = report avanzato
		  
		WE439 (Sabbadin 05/12/2011): si imposta il filtro livello utente in automatico per tutti 
		gli utenti con report personali, indipendentemente dal tipo di report abilitati 
	  */	%>
	<c:if test='${!(fn:contains(listaOpzioniUtenteAbilitate, "ou49"))}'>
		<tr>
			<td class="comandi-dettaglio-sx" colSpan="2">
			&nbsp;<html:checkbox name="testata" property="filtroUtente" value="true" onclick="javascript:filtroSuIdUtente();"></html:checkbox>
			&nbsp;Filtro livello utente
			</td>
		</tr>
	</c:if>
	
<%/* (Giacomazzo 23/02/2015): filtro per ufficio intestatario */ %>
<c:if test='${!(fn:contains(listaOpzioniUtenteAbilitate, "ou49"))}'>
	<c:choose>
		<c:when test="${requestScope.isAssociazioneUffIntAbilitata eq true}">
			<fmt:message key="label.tags.uffint.singolo" var="labelUffInt"/>
			<tr>
				<td class="comandi-dettaglio-sx" colSpan="2">
				&nbsp;<html:checkbox name="testata" property="filtroUfficioIntestatario" value="true" onclick="javascript:filtroPerUfficioIntestatario();"></html:checkbox>
				&nbsp;Filtro per ${fn:toLowerCase(labelUffInt)}
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<html:hidden name="testata" property="filtroUfficioIntestatario" value="false" />
		</c:otherwise>
	</c:choose>
</c:if>
	
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Aggiungi filtro" title="Aggiungi filtro" onclick="javascript:aggiungiFiltro();" >
	      <INPUT type="button" class="bottone-azione" value="Elimina dati selezionati" title="Elimina dati selezionati" onclick="javascript:gestisciSubmit('elimina');" >
      <c:if test="${!empty (sessionScope.recordDettModificato) || !sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}">
      	<INPUT type="button" class="bottone-azione" value="Salva report" title="Salva report nella banca dati" onclick="javascript:salvaRicerca();" >
      </c:if>
			<c:if test="${!empty (sessionScope.recordDettModificato) && !empty (sessionScope.recordDettRicerca.testata.id)}">
				<INPUT type="button" class="bottone-azione" value="Annulla modifica" title="Annulla le modifiche e ricarica il report dalla banca dati" onclick="javascript:ripristinaRicercaSalvata()" >
			</c:if>
			<c:if test="${!empty (sessionScope.recordDettModificato) && empty (sessionScope.recordDettRicerca.testata.id)}">
				<INPUT type="button" class="bottone-azione" value="Annulla inserimento" title="Annulla" onclick="javascript:annullaCreazioneRicerca()" >
			</c:if>
      <c:if test="${empty (sessionScope.recordDettModificato) && sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}">
    	  <INPUT type="button" class="bottone-azione" value="Esegui report" title="Esegui estrazione report" onclick="javascript:eseguiRicerca();" >
      </c:if>
	      &nbsp;
	    </td>
	  </tr>
	</table>
	<input type="hidden" name="metodo" value="eliminaMultiplo"/>
</html:form>