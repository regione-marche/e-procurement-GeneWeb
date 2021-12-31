<%/*
   * Created on 25-ago-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI VISUALIZZAZIONE
  // DEI DATI GENERALI DI UNA RICERCA RELATIVA ALLE AZIONI DI CONTESTO
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
		<gene:historyAdd titolo='Dettaglio schedulazione${titoloHistory}' id="scheda" />
	</gene:insert>
</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modifica(${schedRicForm.idSchedRic});" tabindex="1501" title="Modifica dati generali">Modifica</a>
			</td>
		</tr>	
		<tr>
    	<td>&nbsp;</td>
    	</tr>
		<tr>
			<td class="titolomenulaterale">Gestione schedulazioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaSchedRicWizard();" title="Crea nuova schedulazione" tabindex="1513">Nuovo</a>
			</td>
		</tr>	

	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
