<%/*
   * Created on 20-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA TEMPORANEA
  // DELLA COMPOSIZIONE DEL MODELLO ASSOCIATO ALLA RICERCA CON PROSPETTO
  // CONTENENTE LA  SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript">
  // inoltra la richiesta di composizione dopo aver caricato la pagina e
  // dopo aver reso visibile la gif animata della progressbar, il cui
  // comportamento dipende dal browser
  function gestisciSubmit(){
	  if (navigator.appName == "Microsoft Internet Explorer") {
	    document.parametriProspettoForm.submit();
	    document.getElementById('progressImage').src = "${contextPath}/img/${applicationScope.pathImg}progressbar.gif";
	  } else {
	    setTimeout('document.parametriProspettoForm.submit()', 150);
	  }
	}
</script>