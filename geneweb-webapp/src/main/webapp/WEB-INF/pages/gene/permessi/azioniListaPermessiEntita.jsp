<%/*
       * Created on 23-nov-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA UTENTI
  // ASSOCIATI ALL'ENTITA IN ANALISI CONTENENTE LE AZIONI DI CONTESTO
    %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="codiceLavoro" value="${param.valoreChiave}" />

<gene:template file="menuAzioni-template.jsp">
<%
	/* Inseriti i tag per la gestione dell' history:
	 * il template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito 
	 * solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere 
	 * definiti all'interno del tag <gene:template>
	 */
%>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Condivisione e protezione del lavoro: ${codiceLavoro}' id="listaPermessi" />
	</gene:insert>
</gene:template>

	<tr>
		<td class="titolomenulaterale">Lista: Azioni</td>
	</tr>
	<tr>
		<td class="vocemenulaterale">
			<a href="javascript:modificaCondivisioneLavoro();" title="Modifica condivisione lavoro" tabindex="1500">Modifica condivisione</a>
		</td>
	</tr>
<c:if test='${empty setPermessiPredefiniti}'>
	<tr>
 		<td class="vocemenulaterale"><a href="javascript:impostaCondivisionePredefinita();" tabindex="1501" title="Imposta condivisione predefinita">Imposta condivisione predefinita</a></td>
 	</tr>
</c:if>
	<tr>
  		<td>&nbsp;</td>
 	</tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
