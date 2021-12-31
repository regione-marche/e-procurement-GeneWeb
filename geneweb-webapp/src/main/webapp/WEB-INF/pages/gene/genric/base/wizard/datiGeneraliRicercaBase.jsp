<%
/*
 * Created on 28-apr-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI TROVA RICERCHE
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/DatiGeneraliBase" >
    <html:hidden property="metodo" value="salvaDatiGen"/>
    <html:hidden property="pageFrom" value="${pageFrom}"/>
	<table class="dettaglio-notab">
	  <tr>
      <td class="etichetta-dato">Tipo report (*)</td>
      <td class="valore-dato">
      	<html:select property="tipoRicerca">
	      	<html:options collection="listaTipoRicerca" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Titolo (*)</td>
      <td class="valore-dato"> 
      	<html:text property="nome" styleId="nome" size="50" maxlength="50"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Descrizione</td>
      <td class="valore-dato">
	      <html:text property="descrizione" styleId="descrizione" size="80" maxlength="200" />
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
	      <INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;&nbsp;&nbsp;&nbsp;
	      <INPUT type="button" class="bottone-azione" value="Salva" title="Salva report" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>