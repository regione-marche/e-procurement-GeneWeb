<%/*
   * Created on 04-mag-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE LA PAGINA CON LA DOMANDA DI PUBBLICAZIONE DEL REPORT DI
 // UNA RICERCA BASE
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
   		<b>Pubblicazione</b>
  		<p>
  			Vuoi pubblicare il presente report ad altri utenti o l'utilizzo � personale?
				<br>
		  	<input type="radio" id="pubblicaSi" name="pubblicaReport" <c:if test='${contenitore.numeroGruppi gt 0}'>checked="checked"</c:if>>&nbsp;Si, lo pubblico ad altri utenti
	  		<br>
	  		<input type="radio" id="pubblicaNo" name="pubblicaReport" <c:if test='${contenitore.numeroGruppi eq 0}'>checked="checked"</c:if>>&nbsp;No, il report � ad uso personale
  		</p>
  	</td>
  </tr>
  <tr>
  	<td class="comandi-dettaglio">
      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
	<c:choose>
		<c:when test='${applicationScope.gruppiDisabilitati eq "1"}' >
			<INPUT type="button" class="bottone-azione" value="Fine" title="Fine" onclick="javascript:avanti();">&nbsp;
		</c:when>
		<c:otherwise>
			<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
		</c:otherwise>
	</c:choose>
		</td>
  </tr>
</table>