<%
/*
 * Created on 15-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA DEGLI 
 // UTENTI ASSOCIATI AL GRUPPO IN ANALISI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<table class="dettaglio-tab-lista">
  <tr>
  	<td>
			<display:table name="listaGruppiForm" defaultsort="3" id="listaGruppiForm" class="datilista" sort="list" requestURI="ListaGruppiAccount.do">
				<display:column property="nomeGruppo" title="Nome" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="descrGruppo" title="Descrizione" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="nomeProfilo" title="Profilo" sortable="true" headerClass="sortable"> </display:column>
			</display:table>
		</td>
	</tr>	
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.USRSYS-Scheda.W_GRUPPI.SCHEDAMOD")}'> 
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Modifica associazioni" title="Modifica associazioni" onclick="javascript:modificaAssGruppiAccount('<c:out value='${idAccount}' />')"> &nbsp;
		    &nbsp;
			</td>
	  </tr>
	</c:if>
</c:if>
</table>