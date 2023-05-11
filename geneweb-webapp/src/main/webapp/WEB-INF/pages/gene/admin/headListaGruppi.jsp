<%
/*
 * Created on 27-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA GRUPPI 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<script type="text/javascript">
<!--
	
<!-- elda:jsFunctionOpzioniListaRecord contextPath="${pageContext.request.contextPath}"/-->
	function generaPopupListaOpzioniRecord(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP101#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") && !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>		
					<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica dettaglio"/>
					<elda:jsVocePopup functionJS="elimina('\"+id+\"')" descrizione="Elimina"/>
				</c:if>
			</elda:jsBodyPopup>
			return linkset;
		}

	function creaGruppo(){
		document.location.href='CreaGruppo.do?'+csrfToken;
	}

  function visualizza(id){
		document.location.href='DettaglioGruppo.do?'+csrfToken+'&idGruppo=' + id;
  }

  function modifica(id){
		document.location.href='EditGruppo.do?'+csrfToken+'&idGruppo=' + id;
  }

  function elimina(id) {
  	if(confirm("Procedere con l'eliminazione del record?")){
  		bloccaRichiesteServer();
			document.location.href='GruppoDispatch.do?'+csrfToken+'&metodo=eliminaGruppo&idGruppo=' + id;
	  }
	}
	
-->
</script>

