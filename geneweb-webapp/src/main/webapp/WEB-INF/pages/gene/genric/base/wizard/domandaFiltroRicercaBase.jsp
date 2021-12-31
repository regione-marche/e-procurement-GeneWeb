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

<c:set var="contenitore" value="${sessionScope.recordDettRicerca}" />

<table class="dettaglio-notab">
  <tr>
   	<td colspan="4">
   		<b>Inserimento filtro</b>
  		<p>
  <c:choose>
  	<c:when test='${fn:length(contenitore.elencoFiltri) eq 0}'>
   			Nessun filtro presente. Vuoi inserire una condizione di filtro sui dati?
 		</c:when>
		<c:when test='${fn:length(contenitore.elencoFiltri) eq 1}'>
   			E' presente un filtro. Vuoi aggiungere un'ulteriore condizione di filtro sui dati?
		</c:when>
		<c:otherwise>
   			Sono presenti ${fn:length(contenitore.elencoFiltri)} filtri. Vuoi aggiungere un'ulteriore condizione di filtro sui dati?
		</c:otherwise>
   </c:choose>
	  		<br>
	  		<input type="radio" id="filtroSi" name="domandaFiltro">&nbsp;Si
	  		<br>
	  		<input type="radio" id="filtroNo" name="domandaFiltro" checked="checked">&nbsp;No
  		</p>
  	</td>
  </tr>
  <tr>
  	<td class="comandi-dettaglio">
      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
		<c:if test='${fn:length(contenitore.elencoFiltri) eq 0}'>
	    &nbsp;&nbsp;&nbsp;<INPUT type="button" class="bottone-azione" value="Fine" title="Fine" onclick="javascript:fineWizard();">&nbsp;
  	</c:if>
		</td>
  </tr>
</table>