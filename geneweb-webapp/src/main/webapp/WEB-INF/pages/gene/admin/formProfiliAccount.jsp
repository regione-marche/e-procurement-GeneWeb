<%
/*
 * Created on 12-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
//PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
//PER LA MODIFICA DELL'ASSOCIAZIONE TRA PROFILI E UTENTE IN ANALISI
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/SalvaProfiliAccount" >
	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<display:table  name="listaProfiliUtente" defaultsort="2" id="profiliUtenteForm" class="datilista" requestURI="ListaProfiliAccount.do">
					<display:column title="<center>Associato<br><a href='javascript:selezionaTutti(document.profiliUtenteForm.codiceProfilo);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselezionaTutti(document.profiliUtenteForm.codiceProfilo);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" style="width:50px">
						<html:multibox property="codiceProfilo" >
				       <bean:write name="profiliUtenteForm" property="codiceProfilo"/>
						</html:multibox>
					</display:column>
					<display:column property="nome" title="Nome" sortable="true" headerClass="sortable"></display:column>
					<display:column property="descrizione" title="Descrizione" sortable="true" headerClass="sortable"></display:column>
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	    	<html:hidden property="idAccount" value="${idAccount}" />
	    	<html:hidden property="metodo" value="visualizza" />
	      <INPUT type="button" class="bottone-azione" value="Salva" 	title="Salva modifiche" 	onclick="javascript:salvaModifiche('${idAccount}');">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annullaModifiche('${idAccount}');" >
	        &nbsp;
	    </td>
	  </tr>
	</table>
</html:form>