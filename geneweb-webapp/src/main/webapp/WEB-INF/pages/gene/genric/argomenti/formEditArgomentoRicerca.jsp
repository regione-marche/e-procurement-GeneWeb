<%
/*
 * Created on 07-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA MODIFICA DI UN ARGOMENTO ASSOCIATO AD UNA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/EditArgomentoRicerca" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato">Schema</td>
      <td class="valore-dato"> <c:out value="${tabellaRicercaForm.descrizioneSchema}" />  </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Tabella</td>
      <td class="valore-dato"> ${tabellaRicercaForm.aliasTabella} - ${tabellaRicercaForm.descrizioneTabella} </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Visibile</td>
      <td class="valore-dato"> <html:checkbox property="visibile" /> </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="progressivo" />
      	<html:hidden property="metodo" value="update"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifica();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>