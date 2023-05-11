<%
/*
 * Created on 04-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA ARGOMENTI
 // CONTENENTE LA EFFETTIVA LISTA DEGLI ARGOMENTI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="elencoArgomenti" value="${sessionScope.recordDettRicerca.elencoArgomenti}"  scope="request" />
<c:set var="entitaPrincipale" value="${sessionScope.recordDettRicerca.testata.entPrinc}"  scope="request" />

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<display:table name="elencoArgomenti" class="datilista" id="argomento">
				<display:column title="Opzioni"  >
					<elda:linkPopupRecord idRecord="${argomento.progressivo}" contextPath="${contextPath}" />
				</display:column>
				<display:column title="Arg. Principale" >
				<c:choose>
				<c:when test="${argomento.aliasTabella eq entitaPrincipale}">Si</c:when>
				<c:otherwise>No</c:otherwise>
				</c:choose>
				</display:column>
				<display:column property="descrizioneSchema" title="Schema" >  </display:column>
				<display:column property="mnemonicoTabella" title="Argomento">  </display:column>
				<display:column property="aliasTabella" title="Tabella">  </display:column>
				<display:column property="descrizioneTabella" title="Descrizione">  </display:column>
				<!-- display:column property="visibile" title="Visibile" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator" / -->
			</display:table>
		</td>
	</tr>
	<tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Aggiungi argomento" title="Aggiungi argomento" onclick="javascript:aggiungiArgomento();" >
      <c:if test="${!empty (sessionScope.recordDettModificato) || !sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}">
      	<INPUT type="button" class="bottone-azione" value="Salva report" title="Salva report nella banca dati" onclick="javascript:salvaRicerca();" >
      </c:if>
      <c:if test="${!empty (sessionScope.recordDettModificato) && !empty (sessionScope.recordDettRicerca.testata.id)}">
        <INPUT type="button" class="bottone-azione" value="Annulla modifica" title="Annulla le modifiche e ricarica il report dalla banca dati" onclick="javascript:ripristinaRicercaSalvata()" >
	    </c:if>
	    <c:if test="${!empty (sessionScope.recordDettModificato) && empty (sessionScope.recordDettRicerca.testata.id)}">
	     <INPUT type="button" class="bottone-azione" value="Annulla inserimento" title="Annulla" onclick="javascript:annullaCreazioneRicerca()" >
	    </c:if>
			<c:if test="${empty (sessionScope.recordDettModificato)}">
      	<INPUT type="button" class="bottone-azione" value="Esegui report" title="Esegui estrazione report" onclick="javascript:eseguiRicerca();" >
      </c:if>
      &nbsp;
    </td>
  </tr>
</table>