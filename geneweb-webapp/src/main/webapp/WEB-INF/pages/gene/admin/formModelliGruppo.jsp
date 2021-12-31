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
 
// PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
// PER LA MODIFICA DELL'ASSOCIAZIONE TRA MODELLI E IL GRUPPO IN ANALISI
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/SalvaModelliGruppo" >
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table name="listaModelliAssociatiGruppo" defaultsort="3" id="modelloForm" class="datilista" requestURI="ListaModelliGruppo.do">
					<display:column title="<center>Associato<br><a href='javascript:selezionaTutti(document.modelliGruppoForm.idModello);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.modelliGruppoForm.idModello);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
 						<html:multibox property="idModello" >
				       <bean:write name="modelloForm" property="idModello"/>
						</html:multibox>
					</display:column>
					<display:column property="tipoModello" title="Tipo" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="nomeModello" title="Nome" sortable="true" headerClass="sortable" >  </display:column>
					<display:column property="descrModello" title="Descrizione" sortable="true" headerClass="sortable">  </display:column>
					<display:column property="nomeFile" title="File" sortable="true" headerClass="sortable">  </display:column>
					<display:column property="disponibile" title="Mostra in modelli predisposti" sortable="true" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator"> </display:column>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	    	<html:hidden property="idGruppo" value="${idGruppo}" />
	    	<html:hidden property="metodo" value="visualizzaLista" />
	      <INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:salvaModifiche('<c:out value='${idGruppo}' />')">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annullaModifiche('<c:out value='${idGruppo}' />');" >
	        &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>