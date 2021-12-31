<%
/*
 * Created on 19-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DEI DOCUMENTI
 // ASSOCIATI CONTENENTE LA EFFETTIVA LISTA DEI DOCUMENTI ASSOCIATI E LE 
 // RELATIVE FUNZIONALITA'
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:setIdPagina schema="GENE" maschera="C0OGGASS-Lista" />

<html:form action="/ListaDocumentiAssociati">
	<table class="lista">
		<tr>
			<td>
				<display:table name="listaDocAss" defaultsort="2" id="documentoAssociatoForm" class="datilista" requestURI="ListaDocumentiAssociati.do" pagesize="${requestScope.risultatiPerPagina}" sort="list" defaultorder="descending" >
					<display:column class="associadati" title="Opzioni<br><center><a href='javascript:selezionaTutti(document.listaDocAssForm.id);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.listaDocAssForm.id);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<elda:linkPopupRecord idRecord="${documentoAssociatoForm.id}" contextPath="${contextPath}" />
						<c:if test='${gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL") && sessionScope.entitaPrincipaleModificabile eq "1" && (fn:length(listaDocAss) gt 0)}'>
							<html:multibox property="id">${documentoAssociatoForm.id}</html:multibox>
						</c:if>
				  </display:column>
					<display:column property="dataInserimento" title="Data" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.DataOraDecorator" style="width:135px"></display:column>
					<display:column property="titolo" title="Titolo" sortable="true" headerClass="sortable" ></display:column>
					<display:column title="Nome File" sortable="false" headerClass="sortable"><a href="javascript:mostraDocumento(${documentoAssociatoForm.id});">${documentoAssociatoForm.nomeDocAss}</a>
					</display:column>
				</display:table>
			</td>
		</tr>
	<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" && (gene:checkProtFunz(pageContext, "INS","LISTANUOVO") || gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL") && (not empty listaDocAss && fn:length(listaDocAss) gt 0))}'>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
			<c:if test='${gene:checkProtFunz(pageContext, "INS","LISTANUOVO")}'>
 	      <INPUT type="button" class="bottone-azione" value="Nuovo" title="Nuovo documento" onclick="javascript:inserisciDocumento();" >
	    	&nbsp;
			</c:if>
	    <c:if test='${gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL") && sessionScope.entitaPrincipaleModificabile eq "1" && (fn:length(listaDocAss) gt 0)}'>
	      <INPUT type="button" class="bottone-azione" value="Elimina Selezionati" title="Elimina selezionati" onclick="javascript:apriConfermaEliminaMultipla();" >
        &nbsp;
			</c:if>
	    </td>
	  </tr>
	</c:if>
	</table>
	<input type="hidden" name="metodo" value="eliminaMultiplo"/>
	<input type="hidden" id="cancellazioneFile" name="cancellazioneFile" value="1" />
</html:form>
<html:form action="DocumentoAssociato">
	<input type="hidden" name="metodo" />
	<input type="hidden" name="entita" value="${docAssForm.entita}"/>
<c:forEach items="${docAssForm.valoriCampiChiave}" var="valoriCampi">
	<input type="hidden" name="valoriCampiChiave" value="${valoriCampi}"/>
</c:forEach >
	<input type="hidden" name="key" value="${docAssForm.key}"/>
	<input type="hidden" name="keyParent" value="${docAssForm.keyParent}"/>
</html:form>

<script type="text/javascript">
deselezionaTutti(document.listaDocAssForm.id);
</script>