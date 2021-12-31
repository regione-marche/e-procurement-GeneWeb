<%
/*
 * Created on 23-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA DEGLI 
 // UTENTI ASSOCIATI ALL'ENTITA IN ANALISI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<table class="lista">
  <tr>
  	<td>
			<display:table name="listaPermessiEntita" defaultsort="2" id="PermessoEntitaForm" class="datilista" sort="list" requestURI="ListaPermessiEntita.do">
				<display:column property="idAccount" title="Codice" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="nome" title="Descrizione" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="login" title="Nome" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="autorizzazione" title="Autorizzazione" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="proprietario" title="Proprietario" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator" > </display:column>
				<%@ include file="/WEB-INF/pages/gene/permessi/campiSpecifici.jsp" %>
			</display:table>
		</td>
	</tr>
	<%@ include file="/WEB-INF/pages/gene/permessi/pulsantiListaPermessi.jsp" %>
</table>