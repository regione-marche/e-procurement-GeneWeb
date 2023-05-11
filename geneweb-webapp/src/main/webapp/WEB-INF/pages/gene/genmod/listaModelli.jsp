<%
/*
 * Created on 27-giu-2006
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
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="tmpPageSize" value=""/>
<c:if test='${trovaModelliForm.risPerPagina ne "Tutti"}'>
	<c:set var="tmpPageSize" value="${trovaModelliForm.risPerPagina}"/>
</c:if>

<form name="listaModelli" action="ListaModelli.do">
	<table class="lista">
		<tr>
			<td>
				<display:table name="listaModelli" defaultsort="3" id="modelloForm" class="datilista" requestURI="TrovaModelli.do" pagesize="${tmpPageSize}" sort="list">
					<display:column class="associadati" title="Opzioni<br><center><a href='javascript:selezionaTutti(document.listaModelli.id);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.listaModelli.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<elda:linkPopupRecord idRecord="${modelloForm.idModello}" contextPath="${pageContext.request.contextPath}"/>
						<input type="checkbox" name="id" value="${modelloForm.idModello}"/>
					</display:column>
					<display:column property="tipoModello" title="Tipo documento" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="nomeModello" title="Nome"
													href="Modello.do?metodo=dettaglioModello&" paramId="idModello" paramProperty="idModello" 
													sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="nomeFile" title="File" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="descrModello" title="Descrizione" sortable="true" headerClass="sortable" >  </display:column>
				<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>
				 	<display:column property="personale" title="Modello personale" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="disponibile" title="Presente in Modelli predisposti" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="nomeOwner" title="Utente creatore" sortable="true" headerClass="sortable" >  </display:column>
				</c:if>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Elimina dati selezionati" title="Elimina dati selezionati" onclick="javascript:eliminaSelez();" >
	        &nbsp;
	     </td>
	  </tr>
	</table>
</form>