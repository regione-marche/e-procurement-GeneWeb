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

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<html:form action="/GruppiModello" >
	<html:hidden property="metodo" value="updateGruppiModello"/>
	<html:hidden property="idModello" value="${idModello}"/>
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table name="listaGruppiModello" defaultsort="2" id="gruppiModelli" class="datilista" sort="list" requestURI="GruppiModello.do" >
					<display:column title="<center>Associato<br><a href='javascript:selezionaTutti(document.gruppiModelliForm.gruppiAssociati);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.gruppiModelliForm.gruppiAssociati);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">				
						<input type="checkbox" name="gruppiAssociati" value="${gruppiModelli.idGruppo}" ${gruppiModelli.associato == 1 ? "CHECKED" : ""}/>
					</display:column>
					<display:column property="nomeGruppo" title="Nome" sortable="true" headerClass="sortable">  </display:column>
					<display:column property="descrGruppo" title="Descrizione" sortable="true" headerClass="sortable"> </display:column>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Salva" title="Conferma le modifiche" onclick="javascript:updateListaGruppi()" >
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:listaGruppiModello(${idModello})">
	      <html:hidden property="metodo" value="visualizzaLista" />
	      &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>