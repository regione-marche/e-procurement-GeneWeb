<%
/*
 * Created on 19-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // ARGOMENTI CONTENENTE LA EFFETTIVA LISTA DEI PARAMETRI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="elencoParametri" value="${sessionScope.recordDettRicerca.elencoParametri}"  scope="request" />

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<fmt:setBundle basename="AliceResources"/>

<form name="listaParametri" action="">
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table name="elencoParametri" class="datilista" id="parametro">
					<display:column title="Opzioni" style="width:50px">
						<elda:linkPopupRecord idRecord="${parametro.progressivo}" contextPath="${contextPath}" />
						<input type="checkbox" name="progressivo" value="${parametro.progressivo}" />
					</display:column>
					<display:column property="codiceParametro" title="Codice"> </display:column>
					<display:column property="nome" title="Descrizione per inserimento"> </display:column>
					<display:column property="descrizione" title="Descrizione">  </display:column>
					<display:column title="Tipo"> 
						<fmt:message key="label.tags.uffint.singolo" var="labelUffInt"/>
						<c:set var="nomePar" value="" />
						<c:forEach items="${listaValoriTabellati}" var="tipoPar">
							<c:choose>
								<c:when test='${tipoPar.tipoTabellato eq "UI" and fn:trim(parametro.tipoParametro) eq "UI"}'>
									<c:set var="nomePar" value="${labelUffInt}" />
								</c:when>
								<c:when test='${tipoPar.tipoTabellato eq fn:trim(parametro.tipoParametro)}'>
									<c:set var="nomePar" value="${tipoPar.descTabellato}" />
								</c:when>
								<c:otherwise>
									&nbsp;
								</c:otherwise>
							</c:choose>
						</c:forEach>
	     			<c:out value="${nomePar}"/>
					</display:column>
				<c:if test="${not empty elencoTabellati}" >
					<display:column title="Tabellato"  >
						<c:forEach items="${elencoTabellati}" var="tipoParam">
							<c:if test="${tipoParam.tipoTabellato eq fn:trim(parametro.tabCod)}">
								<c:set var="nomeTab" value="${tipoParam.descTabellato}"/>
							</c:if>
		      	</c:forEach>
	     			<c:out value="${nomeTab}"/>
	     			<c:set var="nomeTab" value="" />
					</display:column>
				</c:if>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
				<c:if test='${requestScope.kronos eq 1 or sessionScope.recordDettRicerca.testata.famiglia eq 4}'>
		      <INPUT type="button" class="bottone-azione" value="Aggiungi parametro" title="Aggiungi un parametro" onclick="javascript:addParametro();" >
	      </c:if>
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