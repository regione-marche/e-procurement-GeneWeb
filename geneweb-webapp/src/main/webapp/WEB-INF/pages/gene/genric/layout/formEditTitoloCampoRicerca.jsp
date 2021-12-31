<%
/*
 * Created on 13-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA MODIFICA DI UN ORDINAMENTO APPARTENENTE AD UNA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/SalvaTitoloColonnaRicerca" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato">Tabella</td>
      <td class="valore-dato">${requestScope.campoRicercaForm.aliasTabella}</td>
    </tr>
    <tr>
      <td class="etichetta-dato">Campo</td>
      <td class="valore-dato">${requestScope.campoRicercaForm.mnemonicoCampo}</td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Titolo colonna (*)</td>
      <td class="valore-dato">
      	<html:text property="titoloColonna" value="${requestScope.campoRicercaForm.titoloColonna}" size="36" maxlength="60"/>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="progressivo"/>
      	<html:hidden property="id" value="${requestScope.campoRicercaForm.progressivo}"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>