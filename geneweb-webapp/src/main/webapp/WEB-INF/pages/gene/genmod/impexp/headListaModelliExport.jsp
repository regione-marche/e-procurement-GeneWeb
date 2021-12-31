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
		if (confirm('<fmt:message key="info.genMod.annullaExport"/>')){
		  document.location.href='AnnullaImportExportModelli.do?'+csrfToken;
		}
	}

  function esportaModello(id){
  	document.location.href='EsportaModello.do?'+csrfToken+'&metodo=esporta&id=' + id;
  }

  
	
	function indietro(){
		document.location.href='InitTrovaModelliExport.do?'+csrfToken;
	}
	
	function elimina(idModello){
	  if(confirm("Procedere con l'eliminazione del modello ?","Eliminazione modello")){
	  	bloccaRichiesteServer();
	 		document.location.href='EsportaModello.do?'+csrfToken+'&metodo=elimina&idModello='+idModello;
		}
	}
-->
</script>