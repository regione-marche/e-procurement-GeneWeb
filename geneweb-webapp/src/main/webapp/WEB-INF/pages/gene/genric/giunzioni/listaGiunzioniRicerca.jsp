<%
/*
 * Created on 11-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA ARGOMENTI
 // CONTENENTE LA EFFETTIVA LISTA DELLE GIUNZIONI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="elencoGiunzioni" value="${sessionScope.recordDettRicerca.elencoGiunzioni}"  scope="request" />

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<display:table name="elencoGiunzioni" class="datilista" id="giunzione">
				<display:column title="Opzioni" style="width:50px">
					<elda:linkPopupRecord idRecord="${giunzione.progressivo}" contextPath="${contextPath}" />
				</display:column>
				<% //!--display:column property="giunzioneAttiva" title="Attiva" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator"--> <!--/display:column--> %>
				<display:column property="aliasTabella1" title="Tabella 1" > </display:column>
				<display:column property="descrizioneTabella1" title="Descrizione"> </display:column>
				<display:column property="campiTabella1" title="Campi Tab. 1"> </display:column>
				<display:column property="tipoGiunzione" title="Join" style="width:30px" decorator="it.eldasoft.gene.commons.web.displaytag.JoinDecorator"> </display:column>
				<display:column property="aliasTabella2" title="Tabella 2"> </display:column>
				<display:column property="descrizioneTabella2" title="Descrizione"> </display:column>
				<display:column property="campiTabella2" title="Campi Tab. 2"> </display:column>
			</display:table>
		</td>
	</tr>	
<!--  -->
	<tr>
    <td class="comandi-dettaglio" colSpan="2">
      <c:if test="${!empty (sessionScope.recordDettModificato) || !sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}">
      <INPUT type="button" class="bottone-azione" value="Salva report" title="Salva report nella banca dati" onclick="javascript:salvaRicerca();" >
	       <c:if test="${!empty (sessionScope.recordDettModificato) && !empty (sessionScope.recordDettRicerca.testata.id)}">
	       <INPUT type="button" class="bottone-azione" value="Annulla modifica" title="Annulla la modifica e ricarica il report dalla banca dati" onclick="javascript:ripristinaRicercaSalvata()" >
	       </c:if>
	       <c:if test="${!empty (sessionScope.recordDettModificato) && empty (sessionScope.recordDettRicerca.testata.id)}">
	       <INPUT type="button" class="bottone-azione" value="Annulla inserimento" title="Annulla" onclick="javascript:annullaCreazioneRicerca()" >
	       </c:if>
      </c:if>
      <c:if test="${empty (sessionScope.recordDettModificato) && sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}">
      	<INPUT type="button" class="bottone-azione" value="Esegui report" title="Esegui estrazione report" onclick="javascript:eseguiRicerca();" >
      </c:if>
      &nbsp;
    </td>
  </tr>
</table>