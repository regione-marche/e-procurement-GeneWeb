<%/*
   * Created on 20-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DOWNLOAD DEL
  // MODELLO COMPOSTO ASSOCIATO AD UNA RICERCA CON PROSPETTO RELATIVA ALLE 
  // AZIONI DI CONTESTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template  file="menuAzioni-template.jsp">
<%
	/* Inseriti i tag per la gestione dell' history:
	 * il template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito 
	 * solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere 
	 * definiti all'interno del tag <gene:template>
	 */
%>
<c:set var="titoloHistory" value=""/>
<c:if test="${fn:length(nomeOggetto) gt 0}" >
<c:set var="titoloHistory" value=" - ${nomeOggetto}"/>
</c:if>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Risultato Report${titoloHistory}' id="risultato" />
	</gene:insert>
</gene:template>
	<c:if test='${! empty parametriProspettoForm.parametriModello}'>
		<tr>
			<td class="titolomenulaterale">Report</td>
		</tr>
	  <tr>
    	<td class="vocemenulaterale">
	    	<a href="javascript:parametriRicerca();" tabindex="1510" title="Vai ai parametri del report">Parametri Report</a>
    	</td>
	  </tr>
		<tr>
    	<td>&nbsp;</td>
   	</tr>
  </c:if>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
