<%
/*
 * Created on 28-lug-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE LA SEZIONE JAVASCRIPT DELLA PAGINA PER DEFINIZIONE
 // DEL FILTRO ENTITA SUI MODELLI
%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!--
  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
    hideMenuPopup();
  }

  function gestisciSubmit() {
  	var strCondizione = trimStringa(document.getElementById("condizioneAggiuntiva").value);
    if(strCondizione != null && strCondizione != ""){
			window.opener.document.modelliForm.filtroEntPrinc.value = strCondizione;
    } else {
	    window.opener.document.modelliForm.filtroEntPrinc.value = "";
	  }
	  
    window.close();
  }

  function annulla(){
  	window.close();
  }
  
  function initPagina(){
  	var str = window.opener.document.modelliForm.filtroEntPrinc.value;
  	if(str != null && str != "")
  		document.getElementById("condizioneAggiuntiva").value = trimStringa(str);
  }

-->
</script>