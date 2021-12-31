<%
/*
 * Created on 22-ago-2007
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="contenitore" value="${sessionScope.recordDettRicerca}" />
<c:choose>
<c:when test='${! empty contenitore.contenitoreDatiRicerca}'>
	<c:set var="datiGenerali" value="${contenitore.contenitoreDatiRicerca.datiGenerali}" />
</c:when>
<c:otherwise>
	<c:set var="datiGenerali" value="${contenitore.contenitoreDatiProspetto.datiGenProspetto.datiGenRicerca}" />
</c:otherwise>
</c:choose>

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!-- 

  function avanti(){
		var esito = true;		 
	<c:if test='${(contenitore.esisteReport and !contenitore.famigliaUguale)}'>
	<% 
	 /* Codice Javascript da eseguire nel seguente caso:
	  * - l'utente ha la gestione personale report, nella basi dati esiste già un
	  *   report con lo stesso titolo (e stesso CODAPP), il report in importazione
	  *   ha la famiglia diversa rispetto al report presente nella base dati;
		* In questo caso l'utente può solamente effettuare l'importazione del report
		* cambiandogli il titolo
	  */
	%>
		if (esito && !controllaCampoInputObbligatorio(document.getElementById("nuovoNome"), 'Nuovo titolo')){
		  esito = false;
		} else {
			var titoloOriginale = "${datiGenerali.nome}";
			var nuovoTitolo = new String(trim(document.getElementById("nuovoNome")));
			if(nuovoTitolo.toUpperCase() != titoloOriginale.toUpperCase()){
				document.opzioniImportForm.nuovoTitolo.value = nuovoTitolo;
		<c:choose>
			<c:when test=''>
				document.opzioniImportForm.tipoImport.value = "insert";
			</c:when>
			<c:otherwise>
				document.opzioniImportForm.tipoImport.value = "insertConNuovoTitolo";
			</c:otherwise>
		</c:choose>
				document.opzioniImportForm.nuovoTitolo.value = document.getElementById("nuovoNome").value;
			} else {
				esito = false;
				alert('Per proseguire specificare un titolo diverso da quello originale');
			}
		}
	</c:if>
	
	<c:if test='${(contenitore.esisteReport and contenitore.famigliaUguale)}' >
	<%
	 /* Codice Javascript da eseguire nel seguente caso:
	  * 1. nella basi dati esiste già un report con lo stesso titolo
	  *    (e stessi CODAPP e PROFILO_OWNER)
		* L'utente può decidere se sovrascrivere il report o se	inserirne uno nuovo
		* con un titolo diverso
	  */
	%>

		var checkBox1 = document.getElementById("sovrascritturaSi").checked;
		var checkBox2 = "";
		try{
			checkBox2 = document.getElementById("sovrascritturaParziale").checked;
		} catch (err){
		}
		
		var checkBox3 = document.getElementById("sovrascritturaNo").checked;
		var titoloOriginale = "${datiGenerali.nome}";
		
		if(checkBox1){
			document.opzioniImportForm.tipoImport.value = "update";
			document.opzioniImportForm.nuovoTitolo.value = "";
		} else if(checkBox2){
			document.opzioniImportForm.tipoImport.value = "updateParziale";
			document.opzioniImportForm.nuovoTitolo.value = "";
		} else if(checkBox3){
			if (esito && !controllaCampoInputObbligatorio(document.getElementById("nuovoNome"), 'Nuovo titolo')){
			  esito = false;
			} else {
				var nuovoTitolo = new String(trim(document.getElementById("nuovoNome")));
				if(nuovoTitolo.toUpperCase() != titoloOriginale.toUpperCase()){
					document.opzioniImportForm.tipoImport.value = "insertConNuovoTitolo";
					document.opzioniImportForm.nuovoTitolo.value = nuovoTitolo;
				} else {
					esito = false;
					alert('Per proseguire specificare un titolo diverso da quello originale');
				}
			}
		} else {
			esito = false;
			alert("Per proseguire selezionare una delle due opzioni");
		}
	</c:if>

		if(esito){
			document.opzioniImportForm.submit();
		}
  }

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaImport"/>')){
			bloccaRichiesteServer();
		  document.location.href='AnnullaImportExport.do?'+csrfToken;
		}
	}
	
	function mostraNuovoTitolo(id){
		if(id == 1)
			document.getElementById("nuovoTitolo").style.display = '';
		else {
			document.getElementById("nuovoTitolo").style.display = 'none';
			document.getElementById("nuovoNome").value="";
		}
	}
	
	function indietro(){
		document.location.href = 'WizardImportRicerca.do?'+csrfToken+'&pageTo=UPL';
	}
	
-->
</script>