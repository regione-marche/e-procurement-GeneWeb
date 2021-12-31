<%/*
       * Created on 12-lug-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA RICERCA
      // CONTENENTE LE AZIONI DI CONTESTO
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
	<gene:historyClear/>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Ricerca report' id="ricerca" />
	</gene:insert>
</gene:template>
		<tr>
			<td class="titolomenulaterale">Trova: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:avviaRicercaRic();" title="Trova report" tabindex="1500">Trova</a></td>
		</tr>	
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:nuovaRicerca();" title="Reset dei campi di ricerca" tabindex="1501">Reimposta</a></td>
		</tr>	
		<!-- tr>
			<td class="vocemenulaterale">
				<a href="javascript:resetRicerca();" title="Ripristino dei campi di ricerca" tabindex="1502">Annulla</a></td>
		</tr-->
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale">Gestione Report</td>
		</tr>
		<!--tr>
			<td class="vocemenulaterale">
				<a href="javascript:apriTrovaRicerche();" title="Trova report" tabindex="1510">Ricerca</a></td>
		</tr-->	
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaRicerca();" title="Crea nuovo report" tabindex="1511">Nuovo</a></td>
		</tr>	
		<!-- tr>
			<td class="vocemenulaterale">
				<a href="javascript:importaRicerca();" title="Importa definizione report" tabindex="1512">Importa report</a></td>
		</tr-->
