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
 // CONTENENTE LA EFFETTIVA LISTA DEI GRUPPI E IL NUMERO DI ASSOCIAZIONI 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
								
<display:table name="listaGruppiConNumeroAssociazioniForm" defaultsort="2" id="gruppoForm" class="datilista" requestURI="ListaGruppi.do" >
	<display:column title="Opzioni" style="width:50px"> <elda:linkPopupRecord idRecord="${gruppoForm.idGruppo}" contextPath="${pageContext.request.contextPath}"/> </display:column>
	<display:column property="nomeGruppo" title="Nome" href="DettaglioGruppo.do?" paramId="idGruppo"  
					paramProperty="idGruppo" sortable="true" headerClass="sortable" >  </display:column>
	<display:column property="descrGruppo" title="Descrizione" sortable="true" headerClass="sortable" >  </display:column>

	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && fn:contains(listaOpzioniUtenteAbilitate, "ou11#")}'>
		<display:column property="numeroUtenti" title="Utenti" sortable="true" headerClass="sortable" >  </display:column>
	</c:if>
	<!-- F.D. 27/02/2007 cambia la gestione dei menù: vengono abilitati i menù in base alle opzioni utente (ou) -->  
	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP2#") && fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>>

		<display:column property="numeroRicerche" title="Report" sortable="true" headerClass="sortable" >  </display:column>
	</c:if>
							
	<c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#") && fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>
		<display:column property="numeroModelli" title="Modelli" sortable="true" headerClass="sortable" >  </display:column>
	</c:if>
</display:table>