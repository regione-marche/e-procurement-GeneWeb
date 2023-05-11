<%/*
   * Created on 26-set-2006
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
  // ESTRAZIONE DELLA RICERCA STESSA (DURANTE LA CREAZIONE/MODIFICA DELLA RICERCA 
  // DALL'AREA APPLICATIVA 'GENERATORE RICERCHE').
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
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Parametri Report' id="param" />
	</gene:insert>
</gene:template>

		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:eseguiRicerca();" tabindex="1501" title="Esegui report">Esegui report</a>
			</td>
		</tr>		
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:svuotaInput();" tabindex="1502" title="Reimposta">Reimposta</a>
			</td>
		</tr>	
		<tr>
			<td class="vocemenulaterale">
		<c:choose>
			<c:when test='${! empty fromPage && fromPage eq "listaRicerche"}'>
				<a href="javascript:vaiListaRicerche();" title="Torna a lista report" tabindex="1513">Torna a lista</a>
			</c:when>
			<c:otherwise>
				<a href="javascript:vaiDettaglioRicerca();" title="Torna a dettaglio report" tabindex="1513">Torna a dettaglio</a>
      </c:otherwise>
	  </c:choose>
			</td>
		</tr>
		<tr>
    	<td>&nbsp;</td>
	  </tr>
		<tr>
			<td class="titolomenulaterale">Gestione Report</td>
		</tr>
		<!--tr>
			<td class="vocemenulaterale">
				<a href="javascript:apriTrovaRicerche();" title="Trova report" tabindex="1510">Ricerca</a>
			</td>
		</tr>	
	  <tr>
    	<td class="vocemenulaterale">
	    	<a href="javascript:listaRicerche();" tabindex="1511" title="Vai a lista report">Lista</a>
    	</td>
	  </tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaRicerca();" title="Crea nuovo report" tabindex="1512">Nuovo</a>
			</td>
		</tr-->	
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
