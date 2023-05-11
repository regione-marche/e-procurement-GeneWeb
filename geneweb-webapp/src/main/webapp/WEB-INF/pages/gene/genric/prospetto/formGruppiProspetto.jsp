<%
/*
 * Created on 15-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA GRUPPI 
 // CONTENENTE LA LISTA DI TUTTI I GRUPPI, CON LO STATO DI ASSOCIAZIONE ALLA RICERCA
 // IN ANALISI
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<html:form action="/SalvaGruppiProspetto" >
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table name="listaGruppiRicerca" id="gruppoForm" class="datilista" >
					<display:column title="<center>Associato<br><a href='javascript:selezionaTutti(document.gruppiRicercaForm.idGruppo);' Title='Seleziona Tutti'> <img src='${contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.gruppiRicercaForm.idGruppo);' Title='Deseleziona Tutti'><img src='${contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:75px">
						<html:multibox property="idGruppo" >
				       <bean:write name="gruppoForm" property="idGruppo"/>
						</html:multibox>
					</display:column>
					<display:column property="nomeGruppo" title="Nome" />
					<display:column property="descrGruppo" title="Descrizione" />
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Modifica associazioni" title="Modifica associazioni" onclick="javascript:gestisciSubmit();">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifiche();">
	      <html:hidden property="metodo" value="visualizzaLista" />
	      &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>