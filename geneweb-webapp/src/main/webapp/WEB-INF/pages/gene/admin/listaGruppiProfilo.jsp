<%
/*
 * Created on 18-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA DEI 
 // GRUPPI ASSOCIATI AL PROFILO IN ANALISI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<table class="dettaglio-tab-lista">
  <tr>
  	<td>
			<display:table name="listaGruppiForm" defaultsort="1" id="listaGruppiForm" class="datilista" sort="list" requestURI="ListaGruppiProfilo.do">
				<display:column property="nomeGruppo" title="Nome" sortable="true" headerClass="sortable"></display:column>
				<display:column property="descrGruppo" title="Descrizione" sortable="true" headerClass="sortable" ></display:column>
			</display:table>
		</td>
	</tr>	
</table>