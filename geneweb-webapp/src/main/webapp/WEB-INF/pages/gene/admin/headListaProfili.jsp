<%
/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA PROFILI 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:template file="menuAzioni-template.jsp">
	<gene:historyClear/>
	<gene:insert name="addHistory">
		<gene:historyAdd titolo='Lista Profili' id="lista" />
	</gene:insert>
</gene:template>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<script type="text/javascript">
<!--
	
<!-- elda:jsFunctionOpzioniListaRecord contextPath="${pageContext.request.contextPath}"/-->
	function generaPopupListaOpzioniRecord(id) {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
				<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza dettaglio"/>
			</elda:jsBodyPopup>
			return linkset;
		}

  function visualizza(id){
		document.location.href='DettaglioProfilo.do?'+csrfToken+'&codPro=' + id;
  }

-->
</script>

