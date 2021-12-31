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
<%@ taglib uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld" prefix="csrf" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="token"><csrf:token/></c:set>

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<html:form action="/ListaRicerche">
	<table class="lista">
		<tr>
			<td>
				<display:table name="listaRicerche" defaultsort="4" id="ricercaForm" class="datilista" requestURI="TrovaRicerche.do?${token}" pagesize="${requestScope.risultatiPerPagina}" sort="list">
					<display:column class="associadati" title="Opzioni<br><center><a href='javascript:selezionaTutti(document.listaForm.id);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.listaForm.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<c:if test='${not fn:contains(ricercaForm.famiglia, "modello") && not fn:contains(ricercaForm.famiglia, "base")}' >
							<elda:linkPopupRecord idRecord="${ricercaForm.idRicerca}" contextPath="${contextPath}" />
						</c:if>
						<c:if test='${fn:contains(ricercaForm.famiglia, "modello")}' >
							<A id="l${ricercaForm.idRicerca}" href="javascript:showMenuPopup('l${ricercaForm.idRicerca}',generaPopupListaOpzioniProspetto('${ricercaForm.idRicerca}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
						</c:if>
						<c:if test='${fn:contains(ricercaForm.famiglia, "base")}' >
							<A id="l${ricercaForm.idRicerca}" href="javascript:showMenuPopup('l${ricercaForm.idRicerca}',generaPopupListaOpzioniBase('${ricercaForm.idRicerca}'));"><IMG src="${contextPath}/img/opzioni_lista.gif" alt="Opzioni" title="Opzioni" height="16" width="16"></A>
						</c:if>
						<html:multibox property="id">${ricercaForm.idRicerca}</html:multibox>
				  </display:column>
					<display:column property="tipoRicerca" title="Tipo Report" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="famiglia" title="Famiglia" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="nomeRicerca" title="Titolo" href="DettaglioRicerca.do?${token}&metodo=visualizza&" paramId="idRicerca"
									paramProperty="idRicerca" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="descrRicerca" title="Descrizione" sortable="true" headerClass="sortable" >  </display:column>
				<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
					<display:column property="personale" title="Report personale" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator" sortable="true" headerClass="sortable" >  </display:column>
				</c:if>
				<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>	
					<display:column property="disponibile" title="Presente nel menù Report" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator" sortable="true" headerClass="sortable" >  </display:column>
				</c:if>					
				<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>					
					<display:column property="owner" title="Utente creatore" sortable="true" headerClass="sortable" >  </display:column>
				</c:if>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Elimina dati selezionati" title="Elimina dati selezionati" onclick="javascript:gestisciSubmit('elimina');" >
	        &nbsp;
	     </td>
	  </tr>
	</table>
	<input type="hidden" name="metodo" value="eliminaMultiplo"/>
</html:form>