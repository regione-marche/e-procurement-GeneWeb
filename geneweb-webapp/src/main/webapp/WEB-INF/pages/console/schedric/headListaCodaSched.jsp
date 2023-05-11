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


<script type="text/javascript">
<!-- 
		
   
  function apriTrovaRicerche(){
		document.location.href='InitTrovaCodaSched.do?'+csrfToken;
  }

  
  function elimina(id) {
  		if(confirm("Procedere con l'eliminazione del record?")){
  			bloccaRichiesteServer();
  			document.location.href='DettaglioCodaSched.do?'+csrfToken+'&metodo=elimina&idCodaSched=' + id;
  		}
  }

	
-->
</script>