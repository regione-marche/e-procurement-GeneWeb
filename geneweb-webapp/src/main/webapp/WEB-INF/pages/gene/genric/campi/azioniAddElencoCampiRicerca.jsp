<%/*
   * Created on 01-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

 // PAGINA CHE CONTIENE LE AZIONI DI CONTESTO DELLA PAGINA DI DETTAGLIO 
 // PER L'INSERIMENTO MULTIPLO DI CAMPI IN UNA RICERCA
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
</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit();" tabindex="1501" title="Conferma">Conferma</a>
			</td>
		</tr>		
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annullaModifiche();" tabindex="1502" title="Annulla">Annulla</a>
			</td>
		</tr>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
