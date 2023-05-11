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

	<table class="lista">
		<tr>
			<td colspan="2">
				<b>Lista report esportabili</b>
	   		<span class="info-wizard">
	   			Di seguito è proposta la lista dei report esportabili.<br>Cliccare sull'icona <img title="Esporta definizione report" alt="Esporta definizione report" src="${contextPath}/img/export.gif" height="16" width="16" />, posta a sinistra della lista, per esportare la definizione del report desiderato.
					Premere "&lt; Indietro" per tornare alla pagina precedente o "Annulla" per annullare l'esportazione guidata e tornare alla pagina iniziale dell'applicativo (homepage).
					Infine, &egrave; possibile eliminare la definizione del report desiderato cliccando sull'icona <img title="Elimina" alt="Elimina report" src="${contextPath}/img/trash.gif" height="16" width="16" />, posta a destra della lista.
	   		</span>
			</td>
		</tr>
		<tr>
			<td>
				<display:table name="listaRicerche" defaultsort="4" id="ricercaForm" class="datilista" requestURI="TrovaRicercheExport.do" pagesize="${requestScope.risultatiPerPagina}" sort="list">
					<display:column style="width:35px">
						<c:if test='${not fn:contains(ricercaForm.famiglia, "modello") && not fn:contains(ricercaForm.famiglia, "base")}' >
							<div align="center"><a href="javascript:esportaRicerca('${ricercaForm.idRicerca}','parametriModelloForm');"><img title="Esporta definizione report" alt="Esporta definizione report" src="${contextPath}/img/export.gif" height="16" width="16" /></a></div>
						</c:if>
						<c:if test='${fn:contains(ricercaForm.famiglia, "modello")}' >
							<div align="center"><a href="javascript:esportaProspetto('${ricercaForm.idRicerca}','parametriModelloForm');"><img title="Esporta definizione report" alt="Esporta definizione report" src="${contextPath}/img/export.gif" height="16" width="16" /></a></div>
						</c:if>
						<c:if test='${fn:contains(ricercaForm.famiglia, "base")}' >
							<div align="center"><a href="javascript:esportaRicerca('${ricercaForm.idRicerca}','parametriModelloForm');"><img title="Esporta definizione report" alt="Esporta definizione report" src="${contextPath}/img/export.gif" height="16" width="16" /></a></div>
						</c:if>
				  </display:column>
					<display:column property="tipoRicerca" title="Tipo Report" sortable="true" headerClass="sortable" >  </display:column>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP98#")}'>	
					<display:column property="famiglia" title="Famiglia" sortable="true" headerClass="sortable" >  </display:column>
				</c:if>
					<display:column property="nomeRicerca" title="Titolo" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="descrRicerca" title="Descrizione" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="owner" title="Utente creatore" sortable="true" headerClass="sortable" >  </display:column>
					<display:column class="associadati" title="" style="width:18px">
						<c:if test='${(codaSchedForm.stato ne 0 )}'>
							<a href='javascript:elimina(${ricercaForm.idRicerca});' Title='Elimina'>
								<img src='${pageContext.request.contextPath}/img/trash.gif' height='15' width='15' alt='Elimina'>
							</a>
						</c:if>
	 			    </display:column>
				</display:table>
			</td>
		</tr>
		<tr>
			<td class="comandi-dettaglio">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
	      <INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
			</td>
		</tr>
	</table>
<html:form action="/ListaRicerche">

</html:form>