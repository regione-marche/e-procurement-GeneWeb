<%
/*
 * Created on 30-mar-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA ACCOUNT
      // CONTENENTE LE AZIONI DI CONTESTO
    %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="menuAzioni-template.jsp">
	<gene:historyClear/>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Ricerca utenti' id="ricerca" />
	</gene:insert>
</gene:template>

		<tr>
			<td class="titolomenulaterale">Trova: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:avviaAccountRic();" title="Trova utenti" tabindex="1500">Trova</a></td>
		</tr>	
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:nuovaRicerca();" title="Reset dei campi di ricerca" tabindex="1501">Reimposta</a></td>
		</tr>	
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale" title="Gestione Utenti">Gestione Utenti</td>
		</tr>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.INS.GENE.USRSYS-Lista.LISTANUOVO")}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovoAccount();" title="Crea nuovo utente" tabindex="1511">Nuovo</a></td>
		</tr>
	</c:if>