<%
/*
 * Created on 14-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE LA SEZIONE JAVASCRIPT DELLA PAGINA 
 // DI RICERCA CAMPI
%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<script type="text/javascript">
<!--
  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideMenuPopup();
  }

  function trova() {
    document.location.href='InitTrovaCampoRicerca.do?'+csrfToken;
  }

  function inserisciCampo(idCampo) {
		bloccaRichiesteServer();
    document.location.href='SalvaCampoTrovatoRicerca.do?'+csrfToken+'&campo='+idCampo;
  }

-->
</script>
