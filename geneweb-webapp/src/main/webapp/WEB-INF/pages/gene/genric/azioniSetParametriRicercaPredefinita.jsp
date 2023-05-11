<%/*
   * Created on 21-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA RELATIVA ALLE AZIONI
  // DI CONTESTO PER IL SETTING DEI PARAMETRI ASSOCIATI AD UN RICERCA IN FASE DI 
  // ESTRAZIONE DELLA RICERCA STESSA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="menuAzioni-template.jsp">
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
		<gene:historyAdd titolo='Parametri Report ${titoloHistory}' id="param" />
	</gene:insert>
</gene:template>
		<tr>
			<td class="titolomenulaterale">Parametri Report: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:eseguiRicerca();" tabindex="1501" title="Esegui report">Esegui Report</a>
			</td>
		</tr>		
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:svuotaInput();" tabindex="1502" title="Reimposta">Reimposta</a>
			</td>
		</tr>	
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" tabindex="1503" title="Torna a lista">Torna a lista</a>
			</td>
		</tr>	
		<tr>
    	<td>&nbsp;</td>
	  </tr>
		<!--tr>
			<td class="titolomenulaterale">Report</td>
		</tr>
	  <tr>
    	<td class="vocemenulaterale">
	    	<a href="javascript:listaRicerchePredefinite();" tabindex="1510" title="Vai a elenco report predefiniti">Elenco report</a>
    	</td>
	  </tr>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr-->
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
