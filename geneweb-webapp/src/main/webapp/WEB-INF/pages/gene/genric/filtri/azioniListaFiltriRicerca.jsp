<%/*
   * Created on 13-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI VISUALIZZAZIONE DELLA LISTA FILTRI DI UNA RICERCA 
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
		<gene:historyAdd titolo='Dettaglio Report${titoloHistory}' id="scheda" />
	</gene:insert>
</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:aggiungiFiltro();" tabindex="1501" title="Aggiungi filtro">Aggiungi filtro</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit('elimina');" title="Elimina dati selezionati" tabindex="1500">Elimina dati selez.</a></td>
		</tr>					
		<c:if test="${!empty (sessionScope.recordDettModificato)}">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:salvaRicerca();" tabindex="1502" title="Salva report nella banca dati">Salva report</a>
			</td>
		</tr>
		<c:if test="${!empty (sessionScope.recordDettRicerca.testata.id)}">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:ripristinaRicercaSalvata();" tabindex="1503" title="Annulla le modifiche e ricarica il report dalla banca dati">Annulla modifica</a>
			</td>
		</tr>		
		</c:if>
		<c:if test="${empty (sessionScope.recordDettRicerca.testata.id)}">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annullaCreazioneRicerca();" tabindex="1504" title="Annulla inserimento">Annulla inserimento</a>
			</td>
		</tr>		
		</c:if>
		</c:if>		
		<c:if test="${empty (sessionScope.recordDettModificato)}">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:eseguiRicerca();" tabindex="1505" title="Esegui estrazione report">Esegui report</a>
			</td>
		</tr>
		</c:if>
		<c:if test="${empty (sessionScope.recordDettModificato)}">
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
	  </tr-->
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaRicerca();" title="Crea nuovo report" tabindex="1512">Nuovo</a>
			</td>
		</tr>
		<!--tr>
			<td class="vocemenulaterale">
				<a href="javascript:esportaRicerca();" title="Esporta definizione report" tabindex="1513">Esporta definizione</a>
			</td>
		</tr-->
		</c:if>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
