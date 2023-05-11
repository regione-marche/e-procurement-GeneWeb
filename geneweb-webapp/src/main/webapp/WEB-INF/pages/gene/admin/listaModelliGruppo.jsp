<%
/*
 * Created on 21-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // DEI MODELLI ASSOCIATI AL GRUPPO IN ANALISI 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
								
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<display:table name="listaModelliGruppo" defaultsort="2" id="modelloForm" class="datilista" sort="list" requestURI="ListaModelliGruppo.do">
				<display:column property="tipoModello" title="Tipo Modello" sortable="true" headerClass="sortable"> </display:column>
<!-- Action provvisoria: da definire quando si sviluppa la  funzionalità Generatore Ricerche -->				
				<display:column property="nomeModello" title="Nome" href="${contextPath}/geneGenmod/Modello.do?metodo=dettaglioModello&admin=1" paramId="idModello" 
						paramProperty="idModello" sortable="true" headerClass="sortable">  </display:column>
				<display:column property="descrModello" title="Descrizione" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="nomeFile" title="File" sortable="true" headerClass="sortable"> </display:column>
				<display:column property="disponibile" title="Mostra in modelli predisposti" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator"> </display:column>
			</display:table>
		</td>
	</tr>
<c:if test='${!fn:contains(listaOpzioniUtenteAbilitate, "ou12#")}'>
	<tr>
    <td class="comandi-dettaglio" colSpan="2">
    	<INPUT type="button" class="bottone-azione" value="Modifica Associazioni" title="Modifica associazioni" onclick="javascript:modificaAssModelliGruppo('${idGruppo}')">
        &nbsp;
    </td>
  </tr>
</c:if>
</table>