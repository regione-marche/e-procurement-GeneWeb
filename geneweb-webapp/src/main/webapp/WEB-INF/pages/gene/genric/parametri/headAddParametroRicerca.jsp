<%/*
   * Created on 19-set-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE AZIONI DI CONTESTO
  // DELLA PAGINA DI CREAZIONE DI UN NUOVO PARAMETRO DA AGGIUNGERE AD UNA RICERCA
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>

<script type="text/javascript">
<!-- 

	// Azioni invocate dal menu contestuale

	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab){
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	//function attivaTabellato(){
	//	var tipoPar = String(document.parametroRicercaForm.tipoParametro.value);
	//	if(document.parametroRicercaForm.tipoParametro.value == 'T'){
	//		document.getElementById("tabellatoCod").style.display = '';
	//	} else {
	//		document.getElementById("tabellatoCod").style.display = 'none';
	//		document.parametroRicercaForm.tabCod.value = null;
	//	}
	//}

	function gestisciSubmit(){
		var esito = false;
		//controllo obsoleto
		//if (    controllaCampoInputObbligatorio(document.parametroRicercaForm.codiceParametro, 'Codice')
		//		&&  controllaCampoInputNoCarSpeciali(document.parametroRicercaForm.codiceParametro, 'Codice')
		//		&&  controllaCampoInputObbligatorio(document.parametroRicercaForm.nome, 'Nome')
		//		&&  controllaLunghezzaInput(document.parametroRicercaForm.descrizione, 'Descrizione', 2000)
		//	  && controllaCampoInputObbligatorio(document.parametroRicercaForm.tipoParametro, 'Tipo')
		//		&& checkTabellato() ){
		if (controllaCampoInputObbligatorio(document.parametroRicercaForm.codiceParametro, 'Codice')
				&& controllaCampoInputObbligatorio(document.parametroRicercaForm.nome, 'Descrizione per inserimento')
				&&  controllaLunghezzaInput(document.parametroRicercaForm.descrizione, 'Descrizione', 2000)){
			esito = true;
		}

    if(esito){
    	bloccaRichiesteServer();
		  document.parametroRicercaForm.submit();
		}
	}

	//function checkTabellato(){
	//	var result = true;
	//	if(document.parametroRicercaForm.tipoParametro.value == 'T'){
	//		 result = controllaCampoInputObbligatorio(document.parametroRicercaForm.tabCod, 'Tabellato')
	//	}
	//	return result;
	//}
	
	function annulla(){
		bloccaRichiesteServer();
		cambiaTab('PAR');
	}

	function initPageParametro(){
		//document.getElementById("tabellatoCod").style.display = 'none';
		document.parametroRicercaForm.codiceParametro.focus();
	}

-->
</script>