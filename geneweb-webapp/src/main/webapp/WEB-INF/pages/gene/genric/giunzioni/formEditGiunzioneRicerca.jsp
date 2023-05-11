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
 // PER LA MODIFICA DI UNA GIUNZIONE ASSOCIATA AD UNA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/EditGiunzioneRicerca" >
	<table class="dettaglio-tab">
	  <% //tr>
      //<td class="etichetta-dato">Attiva</td>
      //<td class="valore-dato"> <html:checkbox property="giunzioneAttiva" /></td>
    //</tr> %>
    <html:hidden property="giunzioneAttiva"/>
    <tr>
      <td class="etichetta-dato">Tabella 1</td>
      <td class="valore-dato"> ${giunzioneRicercaForm.aliasTabella1} </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Descrizione Tab. 1</td>
      <td class="valore-dato">${giunzioneRicercaForm.descrizioneTabella1}
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Campi Tab. 1</td>
      <td class="valore-dato">
      <html:select property="campiTabella1">
      		<html:options name="elencoCampi1" labelName="elencoCampi1"  />
      	</html:select>
    </tr>
    <tr>
      <td class="etichetta-dato">Tipo</td>
      <td class="valore-dato"> 
      	<html:select property="tipoGiunzione">
      		<html:option value="0" > <c:out value="<--> Includi solo le righe in cui i campi collegati da entrambe le tabelle sono uguali"/> </html:option>
      		<html:option value="1" > <c:out value="--> Includi tutti i record di 'Tabella1' e solo i record di 'Tabella2' in cui i campi collegati sono uguali"/> </html:option>
      		<html:option value="2" > <c:out value="<-- Includi tutti i record di 'Tabella2' e solo i record di 'Tabella1' in cui i campi collegati sono uguali"/> </html:option>      		
      	</html:select>
	    </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Tabella 2</td>
      <td class="valore-dato"> ${giunzioneRicercaForm.aliasTabella2}</td>
    </tr>
    <tr>
      <td class="etichetta-dato">Descrizione Tab. 2</td>
      <td class="valore-dato">${giunzioneRicercaForm.descrizioneTabella2}</td>
    </tr>
    <tr>
      <td class="etichetta-dato">Campi Tab. 2</td>
      <td class="valore-dato">
      <html:select property="campiTabella2">
      		<html:options name="elencoCampi2" labelName="elencoCampi2"  />
      	</html:select>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="progressivo" />
      	<html:hidden property="mnemonicoTabella1" value="${giunzioneRicercaForm.mnemonicoTabella1}"/> 
      	<html:hidden property="mnemonicoTabella2" value="${giunzioneRicercaForm.mnemonicoTabella2}"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifica();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>