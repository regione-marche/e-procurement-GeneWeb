<%
/*
 * Created on 22-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI RISULTATO DELLA RICERCA
 // CONTENENTE L'ELENCO EFFETTIVO DEI DATI ESTRATTI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ page
	import="java.util.Vector,
            it.eldasoft.gene.web.struts.genric.risultato.RisultatoRicercaForm,
            it.eldasoft.gene.db.domain.genric.*"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<% RisultatoRicercaForm risultatoRicerca = (RisultatoRicercaForm)request.getAttribute("risultatoRicerca");
   Vector righeRisultato = (risultatoRicerca != null ? risultatoRicerca.getDatiRisultato().getRigheRisultato() : null); 
   int numRecord = (righeRisultato != null ? righeRisultato.size() : 0);
   
%>

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<c:if test="${sessionScope.recordDettRicerca.testata.visParametri}">
<b>Parametri inseriti</b><br/>
<c:set var="indice" value="0"/>
<c:forEach items="${sessionScope.recordDettRicerca.elencoParametri}" var="parametro" varStatus="status">
	${parametro.nome}: 
	<c:if test="${parametro.tipoParametro ne 'T'}">
		${sessionScope.recordDettParametriPerEstrazione[status.index]}
	</c:if>
	<c:if test="${parametro.tipoParametro eq 'T'}">
		<c:set var="listaTabellato" value="${fn:trim(listaListeTabellati[indice])}"/>
		<c:set var="arrayCodVal" value="${fn:split(listaTabellato, '_')}" />
		<c:forEach items="${arrayCodVal}" varStatus="j" step="2">
			<c:if test='${arrayCodVal[j.index] eq sessionScope.recordDettParametriPerEstrazione[status.index]}' >
				${arrayCodVal[j.index+1]}
			</c:if>
		</c:forEach>
		<c:set var="indice" value="${indice+1}" />
	</c:if>
	<br/>
</c:forEach>
</c:if>

<form action="" name="risultatoRicerche" method="post">
<input type="hidden" name="idRicerca" value="${sessionScope.recordDettRicerca.testata.id}">
<table class="lista">
	<tr>
		<td>
			<c:set var="dimensioneRisultato" value="<%=(\"\" + numRecord) %>"/>
			<c:if test="${requestScope.risultatoRicerca.risPerPagina == 0 && dimensioneRisultato > 0}">
				<span class="pagebanner">Trovati <%=numRecord%> record.</span><span class="pagelinks"></span>
			</c:if>
			
		    <display:table name="${requestScope.risultatoRicerca.datiRisultato.righeRisultato}"
					requestURI="${contextPath}/geneGenric/${requestScope.requestURI}.do" export="true" id="currentRowRisultato" 
 					class="datilista" pagesize="${requestScope.risultatoRicerca.risPerPagina}" 
		 			size="${requestScope.risultatoRicerca.datiRisultato.numeroRecordTotali}"
		 			partialList="true" >
			<% if (pageContext.getAttribute("currentRowRisultato_rowNum") != null) {
     int indiceRiga = ((Integer)pageContext.getAttribute("currentRowRisultato_rowNum")).intValue()-1;
     RigaRisultato rigaRisultato = (RigaRisultato)righeRisultato.elementAt(indiceRiga);
%>
			<c:if test="${requestScope.risultatoRicerca.genModelli}">
				<%
  String idPerModello = "";
    for (int cColonne = 0; cColonne < rigaRisultato.getNumeroColonneChiave(); cColonne++) {
      if (cColonne > 0)
        idPerModello += ";";
      idPerModello += (String) rigaRisultato.getColonneChiave().elementAt(cColonne);
  } %>
				<display:column class="associadati"
					title="Opzioni<br><center><a href='javascript:selezionaTutti(document.risultatoRicerche.id);' Title='Seleziona Tutti'> <img src='${contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.risultatoRicerche.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>"
					media="html" style="width:50px">
					<input type="checkbox" name="id" value="<%= idPerModello%>" />
				</display:column>
			</c:if>
			<c:if test="${moduloAttivo ne 'W0' && requestScope.risultatoRicerca.linkScheda}">
				<c:set var="keyRecord" value="${templateDefChiave}" />
				<c:forEach items="${risultatoRicerca.datiRisultato.righeRisultato[pageScope.currentRowRisultato_rowNum-1].colonneChiave}" var="chiave" varStatus="status">
					<c:set var="ricerca" value=":${status.index}"/>
					<c:set var="sostituzione" value=":${chiave}"/>
					<c:set var="keyRecord" value="${fn:replace(keyRecord,ricerca,sostituzione)}"/>
				</c:forEach>
				<display:column class="associadati"
					title=""
					media="html" style="width:24px">
					<a href="${contextPath}/ApriPagina.do?href=${urlScheda}&key=${keyRecord}" title="Apri la scheda di dettaglio"><img src="${contextPath}/img/ico_link.gif" alt="Apri la scheda di dettaglio"/></a>
				</display:column>
			</c:if>

			<% for (int cColonne = 0; cColonne < risultatoRicerca.getNumeroColonne(); cColonne++) {
     String titolo = (String) risultatoRicerca.getTitoliColonne().elementAt(cColonne);
     ElementoRisultato elementoRisultato = (ElementoRisultato)rigaRisultato.getColonneRisultato().elementAt(cColonne); 
     %>
			<display:column title="<%=titolo%>"
						style="<%=elementoRisultato.getFormattazioneHtml()%>"
						value="<%=elementoRisultato.getValore()==null?\"\":elementoRisultato.getValore()%>">
			</display:column>
			<% } %>
			<% } %>

<display:setProperty name="paging.banner.placement" value="both" />
		</display:table>
		</td>
	</tr>
</table>
</form>