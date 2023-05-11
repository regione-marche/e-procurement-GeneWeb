<%
/*
 * Created on 11-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI RISULTATO DELLA RICERCA
 // CONTENENTE L'ELENCO EFFETTIVO DEI DATI ESTRATTI, UTILIZZATO PER LA STAMPA
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
   <table class="lista">
	<tr>
		<td align="right">Dati estratti: <%=numRecord%></td>
	</tr>
	<tr>
		<td><display:table
			name="${requestScope.risultatoRicerca.datiRisultato.righeRisultato}" id="currentRowRisultato"
			class="print-dati">
			<% if (pageContext.getAttribute("currentRowRisultato_rowNum") != null) {
     int indiceRiga = ((Integer)pageContext.getAttribute("currentRowRisultato_rowNum")).intValue()-1;
     RigaRisultato rigaRisultato = (RigaRisultato)righeRisultato.elementAt(indiceRiga);
%>
			<% for (int cColonne = 0; cColonne < risultatoRicerca.getNumeroColonne(); cColonne++) {
     String titolo = (String) risultatoRicerca.getTitoliColonne().elementAt(cColonne);
     ElementoRisultato elementoRisultato = (ElementoRisultato)rigaRisultato.getColonneRisultato().elementAt(cColonne); 
     %>
			<display:column title="<%= titolo%>"
				style="<%=elementoRisultato.getFormattazioneHtml()%>"
				value="<%=(elementoRisultato.getValore()==null?\"\":elementoRisultato.getValore())%>">
			</display:column>
			<% } %>
			<% } %>
		</display:table></td>
	</tr>
</table>
