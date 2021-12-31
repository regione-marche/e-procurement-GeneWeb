<%
/*
 * Created on 21-mar-2007
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


<script type="text/javascript">
<!--
  function downloadFile(){
  	document.parametriProspettoForm.metodo.value="download";
    document.parametriProspettoForm.submit();
  }

  function tornaIndietro(){
  	historyVaiA(0);
  }

	function parametriRicerca(){
		document.location.href='CheckParametriProspetto.do?'+csrfToken+'&idProspetto=${idProspetto}&fromPage=listaPredefinite';
	}

  -->
</script>
