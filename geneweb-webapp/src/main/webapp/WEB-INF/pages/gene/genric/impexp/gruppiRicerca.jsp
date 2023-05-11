<%
/*
 * Created on 23-ag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA 
 // ARGOMENTI CONTENENTE LA EFFETTIVA LISTA DEI GRUPPI ASSOCIATI AD UNA RICERCA 
 // BASE PER IL WIZARD
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<html:form action="/GruppiImport" >
	<table class="lista">
		<tr>
			<td>
				<b>Pubblicazione gruppi</b>
				<span class="info-wizard">
					E' possibile fin da subito pubblicare il report ai gruppi di utenti; basta spuntare le righe dei gruppi ai quali si vuole rendere disponibile l'utilizzo del report.<br>
					Premere "&lt; Indietro" per tornare alla pagina precedente, "Fine" per confermare.
				</span>
			</td>
		</tr>
		<tr>
			<td>
				<display:table name="${listaGruppiRicerca}" id="gruppoForm" class="datilista" >
					<display:column title="Associato">
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
	    <td class="comandi-dettaglio">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
	    	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="button" class="bottone-azione" value="Fine" title="Fine" onclick="javascript:avanti();">&nbsp;
	    </td>
	  </tr>
	</table>
</html:form>