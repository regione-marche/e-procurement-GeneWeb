<%/*
   * Created on 26-apr-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE IL FORM DELLA PAGINA PER LA SELEZIONE DELL'ARGOMENTO
 // DI UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<table class="dettaglio-notab">
  <tr>
   	<td colspan="3">
   		<b>Selezione argomento</b>
   		<span class="info-wizard">
	   		Selezionare l'argomento da cui estrarre i dati.<br>
	   		Premere "Avanti &gt;" per proseguire nella creazione guidata.<br>
	   		Premere "Annulla" per annullare la creazione guidata.
   		</span>
   	</td>
  </tr>
  <tr>
  	<td colspan="1" class="etichetta-dato">Argomento (*)</td>
  	<td colspan="2" class="valore-dato">
  	<html:form action="ArgomentoBase" >
	  	<select name="aliasTabella" id="aliasTabella">
	      <option value=""></option>
	    <c:forEach items="${requestScope.elencoTabelle}" var="tabella">
	      <option value="${tabella.aliasTabella}" <c:if test='${tabella.aliasTabella eq aliasTabellaAttiva}'>selected="selected"</c:if> >${fn:substringAfter(tabella.aliasTabella, "_")} - ${tabella.descrizioneTabella}</option>
	    </c:forEach>
	    </select>
     	<html:hidden property="mnemonicoTabella"/>
	    <html:hidden property="metodo" value="salvaArgomento" />
	  </html:form>
    </td>
  </tr>
  <tr class="comandi-dettaglio">
    <td colspan="3">
      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
	</td>
  </tr>
</table>