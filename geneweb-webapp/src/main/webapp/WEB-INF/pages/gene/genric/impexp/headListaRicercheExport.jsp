<%
/*
 * Created on 04-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TROVA RICERCA 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />
<script type="text/javascript">
<!-- 

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaExport"/>')){
		  document.location.href='AnnullaImportExport.do?'+csrfToken;
		}
	}

  function esportaRicerca(id){
  	document.location.href='EsportaRicerca.do?'+csrfToken+'&metodo=esportaRicerca&id=' + id;
  }

  function esportaProspetto(id){
  	document.location.href='EsportaRicerca.do?'+csrfToken+'&metodo=esportaProspetto&id=' + id;
  }
	
	function indietro(){
		document.location.href='InitTrovaRicercheExport.do?'+csrfToken;
	}
	
	function elimina(id) {
  		if(confirm("Procedere con l'eliminazione del record?")){
  			bloccaRichiesteServer();
  			document.location.href='EsportaRicerca.do?'+csrfToken+'&metodo=elimina&idRicerca=' + id;
  		}
  }
-->
</script>