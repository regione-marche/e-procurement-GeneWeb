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
				<b>Lista modelli esportabili</b>
	   		<span class="info-wizard">
	   			Di seguito è proposta la lista dei modelli esportabili.<br>Cliccare sull' icona <img title="Esporta definizione modello" alt="Esporta definizione modello" src="${contextPath}/img/export.gif" height="16" width="16" />, posta a sinistra della lista, per esportare la definizione del modello desiderato.
					Premere "&lt; Indietro" per tornare alla pagina precedente o "Annulla" per annullare l'esportazione guidata e tornare alla pagina iniziale dell'applicativo (homepage).
					Infine, &egrave; possibile eliminare la definizione del modello desiderato cliccando sull'icona <img title="Elimina" alt="Elimina modello" src="${contextPath}/img/trash.gif" height="16" width="16" />, posta a destra della lista.
	   		</span>
			</td>
		</tr>
		<tr>
			<td>
				<display:table name="listaModelli" defaultsort="4" id="modelloForm" class="datilista" requestURI="TrovaModelliExport.do" pagesize="${requestScope.risultatiPerPagina}" sort="list">
					<display:column style="width:35px">
						<div align="center"><a href="javascript:esportaModello('${modelloForm.idModello}','parametriModelloForm');"><img title="Esporta definizione modello" alt="Esporta definizione report" src="${contextPath}/img/export.gif" height="16" width="16" /></a></div>
				  </display:column>
					<display:column property="tipoModello" title="Tipo documento" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="nomeModello" title="Nome" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="descrModello" title="Descrizione" sortable="true" headerClass="sortable" >  </display:column>
				<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>
				 	<display:column property="personale" title="Modello personale" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="disponibile" title="Presente in Modelli predisposti" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="nomeOwner" title="Utente creatore" sortable="true" headerClass="sortable" >  </display:column>
				</c:if>
				<display:column class="associadati" title="" style="width:18px">
						<c:if test='${(codaSchedForm.stato ne 0 )}'>
							<a href='javascript:elimina(${modelloForm.idModello});' Title='Elimina'>
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
<html:form action="/ListaModelli">

</html:form>