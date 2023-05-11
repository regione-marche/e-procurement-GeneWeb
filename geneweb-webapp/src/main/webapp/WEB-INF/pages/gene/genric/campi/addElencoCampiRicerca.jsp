<%/*
   * Created on 01-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE IL FORM DELLA PAGINA DI DETTAGLIO 
 // PER L'INSERIMENTO MULTIPLO DI CAMPI IN UNA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<table class="dettaglio-tab">
   <tr>
   <td colspan="4">Tabella &nbsp;
     <select name="aliasTabella" id="aliasTabella" onchange="javascript:aggiornaOpzioniSelectCampo(document.getElementById('aliasTabella').selectedIndex);">
       <option value="">&nbsp;</option>
       <c:forEach items="${requestScope.elencoTabelle}" var="tabella">
       <option value="${tabella.aliasTabella}">${tabella.aliasTabella} - ${tabella.descrizioneTabella}</option>
       </c:forEach>
     </select>
   </td>
   </tr>
   <tr>
        <td class="selmultipla">Campi disponibili<br>
                  <select name="campiSelezionabili" id="campiSelezionabili" multiple size="20" onchange="javascript:visualizzaDescrizione(this);">
                  </select>
        </td>
		<td class="bottoni-selmultipla">
			<input type="button" value=" &gt; " class="bottone-dettaglio" title="Seleziona"
			 onclick="javascript:copyOptions(document.getElementById('campiSelezionabili'), document.getElementById('campiSelezionati'));" /><br>
			<input type="button" value="&gt;&gt;" class="bottone-dettaglio" title="Seleziona tutti"
			 onclick="javascript:copyAllOptions(document.getElementById('campiSelezionabili'), document.getElementById('campiSelezionati'));" /><br><br>
			<input type="button" value=" &lt; " class="bottone-dettaglio" title="Deseleziona"
			 onclick="javascript:deleteOptions(document.getElementById('campiSelezionati'));" /><br>
			<input type="button" value="&lt;&lt;" class="bottone-dettaglio" title="Deseleziona tutti"
			 onclick="javascript:deleteAllOptions(document.getElementById('campiSelezionati'));" />
		</td>
        <td class="selmultipla">Campi da inserire<br>
<html:form action="/SalvaElencoCampiRicerca" >
                  <select name="campiSelezionati" id="campiSelezionati" multiple size="20" onchange="javascript:visualizzaDescrizione(this);">
                  </select>
</html:form>
        </td>   
		<td class="bottoni-selmultipla">
			<input type="button" value="Sposta su " class="bottone-dettaglio" title="Sposta su"
			 onclick="javascript:moveUpOptions(document.getElementById('campiSelezionati'));" /><br>
			<input type="button" value="Sposta giù" class="bottone-dettaglio" title="Sposta giù"
			 onclick="javascript:moveDownOptions(document.getElementById('campiSelezionati'));" /><br>
		</td>
   </tr>
   <tr>
   	<td colspan="4">
   		Descrizione campo: <span id="descrCampoSelezionato"></span>
   	</td>
   </tr>
   <tr>
   	<td colspan="4" valign="middle">
   		Trova nell'elenco campi disponibili:&nbsp;<input type="text" id="trovaCampoInElenco" name="trovaCampoInElenco" disabled="disabled">&nbsp;
   		<input type="button" id="pulsanteTrovaCampoInElenco" value="Trova" title="Trova campo nell'elenco" class="bottone-dettaglio" disabled="disabled" onclick="javascript:trovaCampoInElenco();">
   	</td>
   </tr>
   <tr>
     <td class="comandi-dettaglio" colSpan="4">
       <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit()">
       <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifiche()">
       &nbsp;
     </td>
   </tr>
</table>  
