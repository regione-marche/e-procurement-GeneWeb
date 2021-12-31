<%/*
   * Created on 03-ago-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

	// PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA LISTA RICERCHE
	// CONTENENTE LE AZIONI DI CONTESTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="menuAzioni-template.jsp">
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Lista Modelli' id="lista" />
	</gene:insert>
</gene:template>

		<tr>
			<td class="titolomenulaterale">Lista: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:eliminaSelez();" title="Elimina dati selezionati" tabindex="1500">Elimina dati selez.</a></td>
		</tr>	
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale">Gestione Modelli</td>
		</tr>
		<!--tr>
			<td class="vocemenulaterale">
				<a href="javascript:apriTrovaModelli();" title="Trova modelli" tabindex="1510">Ricerca</a></td>
		</tr-->
	  <tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovoModello();" title="Crea nuovo modello" tabindex="1511">Nuovo</a></td>
		</tr>	
		<tr>
			<td>&nbsp;</td>
		</tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
