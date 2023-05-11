<%
/*
 * Created on 10-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA CREAZIONE DI UIN NUOVO CAMPO DA AGGIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/SalvaCampoRicerca" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato">Tabella (*)</td>
      <td class="valore-dato">
        <html:select property="aliasTabella" onchange="javascript:aggiornaOpzioniSelectCampo(document.campoRicercaForm.aliasTabella.selectedIndex);">
          <html:option value=""/>
          <html:options collection="elencoTabelle" property="valuePerSelect" labelProperty="textPerSelect" />
        </html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Campo (*)</td>
      <td class="valore-dato"> 
      	<html:select property="descrizioneCampo" onchange="javascript:aggiornaCampiInput(document.campoRicercaForm.aliasTabella.selectedIndex, document.campoRicercaForm.descrizioneCampo.selectedIndex);">
          <html:option value=""/>
        </html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Funzione</td>
      <td class="valore-dato"> 
                    <html:select property="funzione">
                    <html:option value=""/>
                    <html:option value="COUNT">Conta</html:option>
                    <html:option value="MAX">Massimo</html:option>
                    <html:option value="AVG">Media</html:option>
                    <html:option value="MIN">Minimo</html:option>
                    <html:option value="SUM">Somma</html:option>
                  </html:select>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="mnemonicoCampo"/>
      	<html:hidden property="metodo" value="inserisci"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>
