<%
/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA DEGLI 
 // UTENTI ASSOCIATI AL PROFILO IN ANALISI
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
			<display:table name="listaUtentiForm" defaultsort="1" id="utenteForm" class="datilista" sort="list" requestURI="ListaAccountProfilo.do">
				<display:column property="nome" title="Nome utente" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="login" title="Login" sortable="true" headerClass="sortable"> </display:column>
			</display:table>
		</td>
	</tr>	
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>
	<tr>
    <td class="comandi-dettaglio" colSpan="2">
      		<INPUT type="button" class="bottone-azione" value="Modifica associazioni" title="Modifica associazioni" onclick="javascript:modificaAssUtentiProfilo('${idOggetto}')"> 
      	&nbsp;
    </td>
  </tr>
</c:if>
</table>