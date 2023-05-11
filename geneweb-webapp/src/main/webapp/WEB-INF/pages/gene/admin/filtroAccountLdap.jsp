<%/*
   * Created on 02-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE LA PAGINA CON LA DOMANDA DI DEFINIZIONE FILTRI PER IL WIZARD
 // DI CREAZIONE DI UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="listaOU" value="${filtroAccountLdapForm.listaValueOU}"/>
<html:form action="/ListaAccountLdap.do">
<table class="dettaglio-notab">
<c:choose>
	<c:when test='${! empty listaOU}'>
		<tr>
		 	<td class="etichetta-dato">
				Unit&agrave; organizzativa
			</td>
			<td class="valore-dato">
				<html:select  name="filtroAccountLdapForm" property="filtroOU" styleId="opzioniOU">	
					<option value=""/>
					<html:options property="listaValueOU" labelProperty="listaTextOU"/>		 			
				</html:select>
				
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<html:hidden name="filtroAccountLdapForm" property="filtroOU"/>
	</c:otherwise>
</c:choose>
		<tr>

		 	<td class="etichetta-dato">
				Identificativo utente
			</td>
			<td class="valore-dato">
				<html:text property="filtroCn" />
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
</html:form>