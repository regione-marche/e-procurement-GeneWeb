<%
/*
 * Created on 31-ago-2006
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript" src="${contextPath}/js/forms.js"></script>

<script type="text/javascript">
<!--

// Azioni invocate dal menu contestuale

	function gestisciSubmit() {
		var esito = true;
		if (esito && !controllaCampoInputObbligatorio(parametroRicercaForm.codiceParametro, 'Codice')) {
			esito = false;
		}
		if (esito && !controllaCampoInputObbligatorio(parametroRicercaForm.nome, 'Descrizione per inserimento')) {
			esito = false;
		}
		if (esito && !controllaCampoInputObbligatorio(parametroRicercaForm.tipoParametro, 'Tipo')) {
			esito = false;
		}
		
		if (document.parametroRicercaForm.id.value == "") {
			document.parametroRicercaForm.metodo.value = "insert";
		} else {
			document.parametroRicercaForm.metodo.value = "modifica";
		}
		
		if (esito) {
			bloccaRichiesteServer();
			document.parametroRicercaForm.submit();
		}
	}

	function annulla() {
		bloccaRichiesteServer();
		document.location.href = 'CambiaTabRicercaSql.do?'+csrfToken+'&tab=PAR';
	}
	
	function attivaMenu() {
		var tipoPar = String(document.parametroRicercaForm.tipo.value);
		if(tipoPar == 'M'){
			document.getElementById("trMenu").style.display = '';
			document.getElementById("trTabellato").style.display = 'none';
			document.parametroRicercaForm.tabellato.value = '';
		} else {
			if(tipoPar == 'T'){
				document.getElementById("trTabellato").style.display = '';
				document.getElementById("trMenu").style.display = 'none';
				document.parametroRicercaForm.menu.value = '';
			} else {
				document.getElementById("trMenu").style.display = 'none';
				document.parametroRicercaForm.menu.value = '';
				document.getElementById("trTabellato").style.display = 'none';
				document.parametroRicercaForm.tabellato.value = '';
			}
		}
	}
	
-->
</script>