<%
/*
 * Created on 05-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA CREAZIONE DI UIN NUOVO ARGOMENTO DA AGGIIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/EditArgomentoRicerca" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato">Schema (*)</td>
      <td class="valore-dato">
        <html:select property="mnemonicoSchema"  onchange="javascript:aggiornaOpzioniSelectTabella(document.tabellaRicercaForm.mnemonicoSchema.selectedIndex);">
          <html:option value="">&nbsp;</html:option>
          <c:forEach items="${elencoSchemi}" var="schema">
          	<option value="${schema.codice}">${schema.codice} - ${schema.descrizione}</option>
          </c:forEach>
        </html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Tabella (*)</td>
      <td class="valore-dato"> 
      	<html:select property="descrizioneTabella" onchange="javascript:aggiornaCampiInput(document.tabellaRicercaForm.mnemonicoSchema.selectedIndex, document.tabellaRicercaForm.descrizioneTabella.selectedIndex);">
          <html:option value="">&nbsp;</html:option>
        </html:select>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="mnemonicoTabella"/>
      	<html:hidden property="aliasTabella"/>
      	<html:hidden property="metodo" value="insert"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifica();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>

