<%/*
   * Created on 24-lug-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI VISUALIZZAZIONE
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA ALLE AZIONI DI CONTESTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:setIdPagina schema="GENE" maschera="C0OGGASS-Scheda" />

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
		<gene:historyAdd titolo='Dettaglio documento associato${titoloHistory}' id="scheda" />
	</gene:insert>
</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
	<c:if test='${gene:checkProtFunz(pageContext, "MOD", "MOD") && sessionScope.entitaPrincipaleModificabile eq "1"}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:modifica();" tabindex="1501" title="Modifica documento">Modifica</a>
			</td>
		</tr>
	</c:if>
	  <tr>
	  	<td>&nbsp;</td>
	  </tr>
<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
