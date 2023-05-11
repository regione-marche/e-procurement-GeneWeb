<%/*
   * Created on 21-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA DI ERRORE AVVENUTO DURANTE LA COMPOSIZIONE DEL MODELLO ASSOCIATO
  // AD UNA RICERCA CON PROSPETTO RELATIVA ALLE AZIONI DI CONTESTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
    	<td class="vocemenulaterale">
			<a href="javascript:historyVaiIndietroDi(0);" tabindex="1501" title="Indietro">Indietro</a>
			</td>
   	</tr>
		<tr>
    	<td>&nbsp;</td>
    	</tr>
		<tr>
			<td class="titolomenulaterale">Gestione Report</td>
		</tr>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
