<%/*
       * Created on 25-ago-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
      // DATI GENERALI DI UNA RICERCA (IN FASE DI VISUALIZZAZIONE) CONTENENTE LA 
      // SEZIONE JAVASCRIPT
    %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />
<script type="text/javascript">
<!--

	// Azioni invocate dal menu contestuale

	function creaNuovaSchedRicWizard(){
		document.location.href='WizardSchedRic.do?'+csrfToken+'&pageTo=REP';
	}
	
	
	// Azioni invocate dal tab menu

	function cambiaTab() {
		document.location.href = 'DettaglioSchedRic.do?'+csrfToken+'&metodo=visualizzaDettaglio&idSchedRic=${schedRicForm.idSchedRic}';
	}

	// Azioni di pagina

	function modifica(id){
		document.location.href='DettaglioSchedRic.do?'+csrfToken+'&metodo=modificaSchedulazione&idSchedRic='+id;
	}


-->
</script>