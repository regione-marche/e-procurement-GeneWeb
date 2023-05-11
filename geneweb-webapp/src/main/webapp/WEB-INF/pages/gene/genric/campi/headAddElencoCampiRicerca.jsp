<%
/*
 * Created on 01-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE LA SEZIONE JAVASCRIPT DELLA PAGINA DI DETTAGLIO 
 // PER L'INSERIMENTO MULTIPLO DI CAMPI IN UNA RICERCA
%>
<%@ page import="java.util.Vector,
                it.eldasoft.utils.metadata.domain.Campo,
                it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript">
<!--
	// Azioni invocate dal menu contestuale
	
	// Azioni invocate dal tab menu

	function cambiaTab(codiceTab) {
		document.location.href = 'CambiaTabRicerca.do?'+csrfToken+'&tab=' + codiceTab;
	}

	// Azioni di pagina

	function annullaModifiche() {
		bloccaRichiesteServer();
		cambiaTab('CAM');
	}
	
	function gestisciSubmit() {
		if (document.campiRicercaForm.campiSelezionati.length == 0){
			alert('Selezionare almeno un campo da inserire');
		} else {
			for (var i =  0; i < document.campiRicercaForm.campiSelezionati.length; i++)
			  document.campiRicercaForm.campiSelezionati.options[i].selected = true;
			bloccaRichiesteServer();
			document.campiRicercaForm.submit();
		}
	}
	

<%
/* 
 * Generazione degli array javascript contenenti elenco tabelle e 
 * elenco campi..
 */

	  Vector elencoTabelle = (Vector) request.getAttribute("elencoTabelle");

		String listaTabelle = "var listaTabelle = new Array (";
		for(int i=0; i < elencoTabelle.size(); i++){
		  if(i>0)
		    listaTabelle += ",\n";
		  listaTabelle += "\"" + ((TabellaRicercaForm)elencoTabelle.elementAt(i)).getAliasTabella().replaceAll("[\"]","\\\\\"") + "\"";;
		}
		listaTabelle += ");\n";
		
  Vector elencoCampi = null;
  TabellaRicercaForm tabella = null;
  Campo campo = null;
  String dichiarazioneListaSelect = null;
  String dichiarazioneValueSelect = null;
  for (int i = 0; i < elencoTabelle.size(); i++) {
    tabella = (TabellaRicercaForm) elencoTabelle.elementAt(i);
    dichiarazioneListaSelect = "var " + tabella.getAliasTabella() + " = new Array (";
    dichiarazioneValueSelect = "var valoriCampi" + i + " = new Array (";
    elencoCampi = (Vector) request.getAttribute("elencoCampi" + tabella.getAliasTabella());
    for (int j = 0; j < elencoCampi.size(); j++) {
      campo = (Campo) elencoCampi.elementAt(j);
      if (j>0) {
        dichiarazioneListaSelect += ",\n";
        dichiarazioneValueSelect += ",\n";
      }
      dichiarazioneListaSelect += "\"" + campo.getCodiceMnemonico() + " - " + campo.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
      dichiarazioneValueSelect += "\"" + tabella.getAliasTabella() + "." + campo.getCodiceMnemonico() + "\"";
    }
    dichiarazioneListaSelect += ");\n";
    dichiarazioneValueSelect += ");\n";
	%>

	<%=dichiarazioneListaSelect%>
	<%=dichiarazioneValueSelect%>
	<%
	    }
%>
	<%=listaTabelle%>

	function aggiornaOpzioniSelectCampo(indice) {
		var nomeArrayValue = 'valoriCampi' + (indice-1);
		var nomeArrayText = document.getElementById("aliasTabella").options[indice].value;
		var selectDaAggiornare = document.getElementById("campiSelezionabili");

		if (indice == 0) {
			selectDaAggiornare.length = 0;
			document.getElementById("trovaCampoInElenco").disabled=true;
			document.getElementById("pulsanteTrovaCampoInElenco").disabled=true;	
		} else {
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			selectDaAggiornare.selectedIndex = 0;
			document.getElementById("trovaCampoInElenco").disabled=false;
			document.getElementById("pulsanteTrovaCampoInElenco").disabled=false;			
		}
	}

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
-->
</script>