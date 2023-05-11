<%/*
   * Created on 26-apr-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE RELATIVA ALLE FUNZIONI 
  // JAVASCRIPT DELLA PAGINA DI DEFINIZIONE DEI CAMPI DI UNA RICERCA BASE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />

<%@ page import="java.util.Vector,
                it.eldasoft.utils.metadata.domain.Campo,
                it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!-- 

<%
/* 
 * Generazione degli array javascript contenenti elenco tabelle e 
 * elenco campi..
 */

  Vector elencoCampi = null;
  TabellaRicercaForm tabella = null;
  Campo campo = null;
  String dichiarazioneListaSelect = null;
  String dichiarazioneValueSelect = null;
  //for (int i = 0; i < elencoTabelle.size(); i++) {
    tabella = (TabellaRicercaForm) request.getAttribute("tabella");
    dichiarazioneListaSelect = "var " + tabella.getAliasTabella() + " = new Array (";
    dichiarazioneValueSelect = "var valoriCampi = new Array (";
    elencoCampi = (Vector) request.getAttribute("elencoCampi");
    for (int j = 0; j < elencoCampi.size(); j++) {
      campo = (Campo) elencoCampi.elementAt(j);
      if (j>0) {
        dichiarazioneListaSelect += ",\n";
        dichiarazioneValueSelect += ",\n";
      }
      dichiarazioneListaSelect += "\"" + campo.getCodiceMnemonico().substring(campo.getCodiceMnemonico().indexOf("_")+1) + " - " + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
      dichiarazioneValueSelect += "\"" + tabella.getAliasTabella() + "." + campo.getCodiceMnemonico() + "\"";
    }
    dichiarazioneListaSelect += ");\n";
    dichiarazioneValueSelect += ");\n";
	%>

	<%=dichiarazioneListaSelect%>
	<%=dichiarazioneValueSelect%>

	// Azioni di pagina

	function avanti(){
		var campiSelezionati = document.getElementById("campiSelezionati");
		if(campiSelezionati.length == 0){
			alert("Selezionare almeno un campo");
		} else {
			for (var i =  0; i < document.campiRicercaForm.campiSelezionati.length; i++)
			  document.campiRicercaForm.campiSelezionati.options[i].selected = true;

			bloccaRichiesteServer();
			document.campiRicercaForm.submit();
		}
	}
	
	function indietro(){
	  document.location.href='WizardBase.do?'+csrfToken+'&pageTo=ARG';
	}

	function fineWizard(){
		var campiSelezionati = document.getElementById("campiSelezionati");
		if(campiSelezionati.length == 0){
			alert("Selezionare almeno un campo");
		} else {
			for (var i =  0; i < document.campiRicercaForm.campiSelezionati.length; i++)
			  document.campiRicercaForm.campiSelezionati.options[i].selected = true;
			
			document.getElementById("pageFrom").value = "CAM";
			bloccaRichiesteServer();
			document.campiRicercaForm.submit();
		}
	}

	function annulla(){
		if (confirm('<fmt:message key="info.genRic.annullaCreazione"/>')){
			bloccaRichiesteServer();
			document.location.href = 'DettaglioRicerca.do?'+csrfToken+'&metodo=annullaCrea';
		}
	}

	// posizione della opzione selezionata nella select delle tabelle
	var indexTabellaSelezionata = -1;

	function visualizzaDescrizione(unCampoDiInput){
		var indice = unCampoDiInput.selectedIndex;
		var testo = unCampoDiInput.options[indice].text;
		var stampaDescr = true;
		var pos = testo.indexOf("-");
		for(var i=0; i < unCampoDiInput.length && stampaDescr; i++){
			if(unCampoDiInput.options[i].selected && unCampoDiInput.options[i].value != "")
				if(indice != i)
					stampaDescr = false;
		}
		if(stampaDescr)
			document.getElementById("descrCampoSelezionato").innerHTML = testo.substring(pos+1, testo.length);
		else
			document.getElementById("descrCampoSelezionato").innerHTML = "";
	}
	
	function trovaCampoInElenco(){
		var campoDiInput = document.getElementById("trovaCampoInElenco");
		if(controllaCampoInputObbligatorio(campoDiInput, "Trova campo nell'elenco")){
			var testoInput = new String(trim(campoDiInput)).toLowerCase();
			var numeroCampiSelezionabili = document.getElementById("campiSelezionabili").length;
			var testoCampo = "";
			var numeroOccorrenze = 0;
			for(var i=0; i < numeroCampiSelezionabili; i++){
				testoCampo = new String(document.getElementById("campiSelezionabili").options[i].text).toLowerCase();
				if(testoCampo.indexOf(testoInput) >= 0){
					document.getElementById("campiSelezionabili").options[i].selected=true;
					numeroOccorrenze++;
				} else
					document.getElementById("campiSelezionabili").options[i].selected=false;
			}
			if(numeroOccorrenze == 0){
				document.getElementById("descrCampoSelezionato").innerHTML = "";
				alert('Nessun campo nella lista dei campi selezionabili contiene la stringa specificata');
			} else if(numeroOccorrenze > 1){
				document.getElementById("descrCampoSelezionato").innerHTML = "";
				alert('Attenzione: la ricerca ha selezionato ' + numeroOccorrenze + ' campi');
			} else {
				visualizzaDescrizione(document.getElementById("campiSelezionabili"));
			}
		}
	}

  function initPageInEdit(){ 
  	// Popolamento della select campi selezionabili con tutti i campi
  	var indice = 1;
 		var nomeArrayValue = 'valoriCampi' + (indice-1);
		var selectCampiSelezionabili = document.getElementById("campiSelezionabili");
		var selectCampiSelezionati = document.getElementById("campiSelezionati");
		
		indexTabellaSelezionata = indice;
		selectCampiSelezionabili.selectedIndex = 0;
		document.getElementById("trovaCampoInElenco").disabled=false;
		document.getElementById("pulsanteTrovaCampoInElenco").disabled=false;			

		// Ora devo cancellare dalla select dei campi selezionabili i campi presenti 
		// nella select dei campi selezionati
  	var array = new Array();
  	var index = 0;
  	var valoreOpzione = "";
		var campoTrovato = false;
  	for(var i=0; i < selectCampiSelezionabili.length; i++){
  		valoreOpzione = selectCampiSelezionabili.options[i].value;
			campoTrovato = false;
  		for(var j=0; j < selectCampiSelezionati.length && !campoTrovato; j++){
  			if(valoreOpzione == selectCampiSelezionati.options[j].value){
  				array[index] = i;
  				campoTrovato = true;
  				index++;
  			}
  		}
  	}
  	
  	// Nella variabile array sono presenti gli indice dei campi da cancellare 
  	// nella select dei campi selezionabili
  	for(var i=0; i < array.length; i++)
  		deleteOption(selectCampiSelezionabili, array[i]-i);
  }

-->
</script>