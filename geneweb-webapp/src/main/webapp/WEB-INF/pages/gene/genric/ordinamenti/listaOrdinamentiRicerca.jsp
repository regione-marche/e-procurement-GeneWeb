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
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="elencoOrdinamenti" value="${sessionScope.recordDettRicerca.elencoOrdinamenti}"  scope="request" />

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<html:form action="/ListaOrdinamentiRicerca">
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table name="elencoOrdinamenti" class="datilista" id="ordinamento">
					<display:column title="Opzioni<br><center><a href='javascript:selezionaTutti(document.listaForm.id);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.listaForm.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<elda:linkPopupRecord idRecord="${ordinamento.progressivo}" contextPath="${contextPath}" />
						<input type="checkbox" name="id" value="${ordinamento.progressivo}" />
					</display:column>
					<display:column property="aliasTabella" title="Tabella" > </display:column>
					<display:column property="mnemonicoCampo" title="Campo"> </display:column>
					<display:column property="ordinamento" title="Ordinamento" decorator="it.eldasoft.gene.commons.web.displaytag.OrdinamentoDecorator">  </display:column>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Aggiungi ordinamento" title="Aggiungi ordinamento" onclick="javascript:aggiungiOrdinamento();" >
	      <INPUT type="button" class="bottone-azione" value="Elimina dati selezionati" title="Elimina dati selezionati" onclick="javascript:gestisciSubmit('elimina');" >
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
<input type="hidden" name="metodo" value="eliminaMultiplo"/>
</html:form>