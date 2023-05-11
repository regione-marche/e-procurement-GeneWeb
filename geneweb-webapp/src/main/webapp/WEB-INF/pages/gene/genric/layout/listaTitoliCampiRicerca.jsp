<%
/*
 * Created on 12-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // ARGOMENTI CONTENENTE LA EFFETTIVA LISTA DEGLI ORDINAMENTI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="testata" value="${sessionScope.recordDettRicerca.testata}" scope="request" />
<c:set var="elencoCampi" value="${sessionScope.recordDettRicerca.elencoCampi}" scope="request" />

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<form name="listaCampi" action="">
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table name="elencoCampi" class="datilista" id="campo">
					<display:column title="Opzioni" style="width:50px">
						<elda:linkPopupRecord idRecord="${campo.progressivo}" contextPath="${contextPath}" />
						<input type="checkbox" name="progressivo" value="${campo.progressivo}" />
					</display:column>
					<display:column property="aliasTabella" title="Tabella" />
					<display:column property="mnemonicoCampo" title="Campo" />
					<display:column property="titoloColonna" title="Titolo colonna" />
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica" onclick="javascript:modificaTutti();" >
	      <c:if test="${!empty (sessionScope.recordDettModificato) || !sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}">
		      <INPUT type="button" class="bottone-azione" value="Salva report" title="Salva report nella banca dati" onclick="javascript:salvaRicerca();" >
				</c:if>
				<c:if test="${!empty (sessionScope.recordDettModificato) && !empty (sessionScope.recordDettRicerca.testata.id)}">
					<INPUT type="button" class="bottone-azione" value="Annulla modifica" title="Annulla la modifica e ricarica il report dalla banca dati" onclick="javascript:ripristinaRicercaSalvata()" >
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
</form>