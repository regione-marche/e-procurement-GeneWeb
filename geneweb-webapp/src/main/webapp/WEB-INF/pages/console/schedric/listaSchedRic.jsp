<%
/*
 * Created on 03-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA GRUPPI 
 // CONTENENTE LA EFFETTIVA LISTA DEI GRUPPI E LE RELATIVE FUNZIONALITA' 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<html:form action="/ListaSchedRic">
	<table class="lista">
		<tr>
			<td>
				<display:table name="listaSchedRic" defaultsort="2" id="schedRicForm" class="datilista" requestURI="TrovaSchedRic.do" pagesize="${requestScope.risultatiPerPagina}" sort="list">
					<display:column class="associadati" title="Opzioni<br><center><a href='javascript:selezionaTutti(document.listaForm.id);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.listaForm.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<c:choose>
							<c:when test='${schedRicForm.attivo eq 1}' >
								<A id="l${schedRicForm.idSchedRic}" href="javascript:showMenuPopup('l${schedRicForm.idSchedRic}',generaPopupListaOpzioniDisattiva('${schedRicForm.idSchedRic}'));"><IMG src="${pageContext.request.contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
							</c:when>
							<c:otherwise>
								<A id="l${schedRicForm.idSchedRic}" href="javascript:showMenuPopup('l${schedRicForm.idSchedRic}',generaPopupListaOpzioniAttiva('${schedRicForm.idSchedRic}'));"><IMG src="${pageContext.request.contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
							</c:otherwise>
						</c:choose>
						<html:multibox property="id">${schedRicForm.idSchedRic}</html:multibox>
				  </display:column>
					<display:column property="nome" title="Nome" sortable="true" headerClass="sortable" href="DettaglioSchedRic.do?metodo=visualizzaDettaglio&" paramId="idSchedRic"
									paramProperty="idSchedRic">  </display:column>
					<display:column property="nomeRicerca" title="Report" sortable="true" headerClass="sortable"></display:column>
					<display:column property="descTipo" title="Frequenza" sortable="true" headerClass="sortable"></display:column>
					<display:column property="attivo" title="Attivo" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator"></display:column>
					
				<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou62#")}'>					
					<display:column property="nomeOwner" title="Utente creatore" sortable="true" headerClass="sortable"></display:column>
					<display:column property="nomeEsecutore" title="Utente esecutore" sortable="true" headerClass="sortable"></display:column>
				</c:if>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Elimina Dati Selezionati" title="Elimina dati selezionati" onclick="javascript:gestisciSubmit('elimina');" >
	        &nbsp;
	     </td>
	  </tr>
	</table>
	<input type="hidden" name="metodo" value="eliminaMultiplo"/>
</html:form>