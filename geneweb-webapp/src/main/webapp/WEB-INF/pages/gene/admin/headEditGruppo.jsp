<%
/*
 * Created on 30-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO 
 // GRUPPO (IN FASE DI EDITING) CONTENENTE LA SEZIONE JAVASCRIPT 
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript" >
<!--

//	function creaGruppo(){
//		document.location.href='CreaGruppo.do';
//	}

//	function listaGruppi(){
//		document.location.href='ListaGruppi.do';
//	}

  function annullaModifica(id){
	  bloccaRichiesteServer();
		document.location.href='DettaglioGruppo.do?'+csrfToken+'&idGruppo=' + id;
  }

	function gestisciSubmit() {
	  var esito = true;
	  
	  if (esito && !controllaCampoInputObbligatorio(gruppoForm.nomeGruppo, 'Nome')){
		  esito = false;
		}

		if (esito){
			bloccaRichiesteServer();
			document.gruppoForm.submit();
		}
	}

-->
</script>