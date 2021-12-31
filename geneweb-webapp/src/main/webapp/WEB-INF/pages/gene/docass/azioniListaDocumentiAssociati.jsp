<%/*
   * Created on 19-lug-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA LISTA DOCUMENTI
  // ASSOCIATI CONTENENTE LE AZIONI DI CONTESTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:setIdPagina schema="GENE" maschera="C0OGGASS-Lista" />

<gene:template file="menuAzioni-template.jsp">
<%
	/* Inseriti i tag per la gestione dell' history:
	 * il template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito 
	 * solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere 
	 * definiti all'interno del tag <gene:template>
	 */
%>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Lista documenti associati' id="listaDocAss" />
	</gene:insert>
</gene:template>
		<tr>
			<td class="titolomenulaterale">Lista: Azioni</td>
		</tr>
	<c:if test='${gene:checkProtFunz(pageContext, "INS","LISTANUOVO") && sessionScope.entitaPrincipaleModificabile eq "1"}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:inserisciDocumento();" title="Nuovo documento" tabindex="1500">Nuovo</a></td>
		</tr>
	</c:if>
	<c:if test='${gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL") && sessionScope.entitaPrincipaleModificabile eq "1" && (fn:length(listaDocAss) gt 0)}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:apriConfermaEliminaMultipla();" title="Elimina selezionati" tabindex="1501">Elimina selezionati</a></td>
		</tr>
	</c:if>
		<tr>
			<td>&nbsp;</td>
		</tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
