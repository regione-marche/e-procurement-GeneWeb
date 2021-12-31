<%
/*
 * Created on: 13-Lug-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista degli uffici intestatari */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint && fn:contains(archiviFiltrati,'UFFINT')}">
	<c:set var="filtroUffint" value="CODEIN = '${sessionScope.uffint}'"/>
</c:if>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaParametrizzata" scope="request">
	<fmt:message key="label.tags.uffint.multiplo" />
</c:set>
<c:set var="nomeEntitaSingolaParametrizzata" scope="request">
	<fmt:message key="label.tags.uffint.singolo" />
</c:set>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ListaUffint">
	<gene:setString name="titoloMaschera" value="Lista ${fn:toLowerCase(nomeEntitaParametrizzata)}"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.SchedaUffint")}' scope="request"/>

	<gene:redefineInsert name="corpo">
		<c:if test='${(not empty filtroUffint) || fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
			<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
			<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteListaInserisci"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
		</c:if>
		
		<gene:set name="titoloMenu" scope="requestScope">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr><td >
		<gene:formLista entita="UFFINT" where="${filtroUffint}" sortColumn="3" pagesize="20" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreUFFINT">
			<jsp:include page="uffint-interno-lista.jsp" />
		</gene:formLista>
		</td></tr>
		<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		</table>
  </gene:redefineInsert>
  <gene:javaScript>
  	function attivazione(chiave,operazione){
		var codice= chiave.substring(chiave.indexOf(":") + 1);
		var href = "href=gene/uffint/popupAttivazione.jsp&codice=" + codice + "&operazione=" + operazione+ "&numeroPopUp=1";
		openPopUpCustom(href, "Attivazione", "480", "250", "no", "no");
	}
  </gene:javaScript>
</gene:template>
