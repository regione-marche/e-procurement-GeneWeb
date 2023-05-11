<%
/*
 * Created on 17-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
//PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
//PER LA MODIFICA DELL'ASSOCIAZIONE TRA UTENTI E IL GRUPPO IN ANALISI
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<form name="listaAccountLdap" action="ListaAccountLdap.do">
	<table class="lista">
		<tr>
			<td>
				<display:table  name="listaAccountLdap" defaultsort="2" id="accountLdap" class="datilista" requestURI="ListaAccountLdap.do" pagesize="20" sort="list">
					<display:column title="Opzioni"><input type="radio" name="dn" value="${accountLdap.dn}"/></display:column>
					<display:column property="cn" title="Nome" sortable="true" headerClass="sortable" />
					<display:column property="dn" title="Identificativo utente LDAP" sortable="true" headerClass="sortable" />
				</display:table>
			</td>
		</tr>
		<tr>
	    <td class="comandi-dettaglio" colSpan="2">
	    	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
	    	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
		    <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
	    </td>
	  </tr>
	</table>
</form>
<form name="idForm" action="CreaAccountLdap.do" method="POST">
	<input type="hidden" name="id" value="">
</form>