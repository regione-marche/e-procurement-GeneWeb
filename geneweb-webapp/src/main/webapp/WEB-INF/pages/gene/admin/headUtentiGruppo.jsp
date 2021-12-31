<%
/*
 * Created on 17-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE LA SEZIONE JAVASCRIPT DELL'ISTANZA DELLA SOTTOPARTE DELLA 
 // PAGINA DI MODIFICA ASSOCIAZIONE UTENTI-GRUPPO 
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
<!--
	
//	function creaGruppo(){
//		document.location.href='CreaGruppo.do';
//	}

//	function listaGruppi(){
//		document.location.href='ListaGruppi.do';
//	}
	
  function salvaModifiche(id){
	  bloccaRichiesteServer();
  	document.utentiGruppoForm.submit();
  }

  function annullaModifiche(id){
	  bloccaRichiesteServer();
		document.location.href='ListaUtentiGruppo.do?'+csrfToken+'&metodo=visualizzaLista&idGruppo=' + id;
  }
	
-->
</script>

