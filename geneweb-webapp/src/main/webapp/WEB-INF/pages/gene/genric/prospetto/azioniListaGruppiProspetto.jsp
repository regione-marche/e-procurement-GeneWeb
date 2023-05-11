<%/*
   * Created on 13-mar-2007
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

		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modifica();" tabindex="1501" title="Modifica">Modifica</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:eseguiRicerca();" tabindex="1505" title="Esegui estrazione report">Esegui report</a>
			</td>
		</tr>
		<tr>
    	<td>&nbsp;</td>
	  </tr>
		<tr>
			<td class="titolomenulaterale">Gestione Report</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaRicerca();" title="Crea nuovo report" tabindex="1513">Nuovo</a>
			</td>
		</tr>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
